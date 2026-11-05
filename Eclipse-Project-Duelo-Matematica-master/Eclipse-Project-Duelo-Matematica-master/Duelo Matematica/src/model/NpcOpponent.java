package model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Story-mode bot. For every question it spins a roulette wheel to decide whether it
 * will be right, then "answers" after a random human-like delay.
 *
 * The callback runs on a background thread: wrap UI work in Platform.runLater(...).
 *
 * STATUS: NEW FILE in package model. Used by GameStoryController for story levels 1-3.
 *
 * LEVELS (the StoryLevel enum)
 *   LEVEL_1  20% correct / 80% fail   thinks 4-9 s
 *   LEVEL_2  40% correct / 60% fail   thinks 3-7 s
 *   LEVEL_3  50% correct / 50% fail   thinks 2-5 s
 * The percentages are the design requirement. The thinking times are tunable guesses.
 *
 * HOW IT WORKS (per question)
 *   1. The controller calls answerQuestion(correctIndex, optionCount, timeLimitMs, callback).
 *   2. Roulette: a random number 0-99 is drawn. Below the level's percentage = correct.
 *   3. If the result is "fail", a random WRONG option is chosen (never the correct one).
 *   4. A random delay between the level's min and max is chosen, clamped so the answer lands
 *      between 300 ms and (time limit - 300 ms), never after the buzzer.
 *   5. After the delay the callback receives an NpcAnswer (correct?, chosenIndex, delayMs).
 *   The delay is passed to GameStoryModel.applyOpponentMove, so the bot gets the same
 *   speed-bonus rules as the player.
 *
 * LIFECYCLE
 *   cancel()    drops the pending answer (player answered first, or the question ended).
 *   shutdown()  cancel plus stop the worker thread; call it when leaving the screen.
 *   Calling answerQuestion again automatically cancels the previous pending answer.
 *
 * Pure game logic: no JavaFX in here. The callback arrives on a background thread.
 */
public class NpcOpponent {

    /** Story levels: percent chance of a correct answer, and how long the bot "thinks". */
    public enum StoryLevel {
        LEVEL_1(20, 4000, 9000),   // 20% correct / 80% fail
        LEVEL_2(40, 3000, 7000),   // 40% correct / 60% fail
        LEVEL_3(50, 2000, 5000);   // 50% correct / 50% fail

        private final int correctPercent;
        private final long minDelayMs;
        private final long maxDelayMs;

        StoryLevel(int correctPercent, long minDelayMs, long maxDelayMs) {
            this.correctPercent = correctPercent;
            this.minDelayMs = minDelayMs;
            this.maxDelayMs = maxDelayMs;
        }

        public int getCorrectPercent() { return correctPercent; }

        /** 1, 2 or 3 -> the matching level. */
        public static StoryLevel fromNumber(int level) {
            switch (level) {
                case 1: return LEVEL_1;
                case 2: return LEVEL_2;
                case 3: return LEVEL_3;
                default: throw new IllegalArgumentException("Story level must be 1-3, got " + level);
            }
        }
    }

    /** What the bot decided for one question. */
    public static final class NpcAnswer {
        private final boolean correct;
        private final int chosenIndex;
        private final long delayMs;

        NpcAnswer(boolean correct, int chosenIndex, long delayMs) {
            this.correct = correct;
            this.chosenIndex = chosenIndex;
            this.delayMs = delayMs;
        }

        public boolean isCorrect() { return correct; }
        public int getChosenIndex() { return chosenIndex; }
        public long getDelayMs() { return delayMs; }
    }

    private final StoryLevel level;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "npc-opponent");
        t.setDaemon(true);
        return t;
    });
    private ScheduledFuture<?> pending;

    public NpcOpponent(StoryLevel level) {
        this.level = level;
    }

    public NpcOpponent(int levelNumber) {
        this(StoryLevel.fromNumber(levelNumber));
    }

    /**
     * Call when a question is shown. The bot spins the roulette now and delivers its answer
     * after the delay (unless cancel() is called first, or the time limit would run out).
     *
     * @param correctIndex index of the right option
     * @param optionCount  how many options the question has (needed to pick a wrong one)
     * @param timeLimitMs  question timer, the bot always answers before it ends
     * @param onAnswer     receives the bot's answer
     */
    public synchronized void answerQuestion(int correctIndex, int optionCount, long timeLimitMs,
                                            Consumer<NpcAnswer> onAnswer) {
        cancel();

        ThreadLocalRandom rng = ThreadLocalRandom.current();

        // Roulette: wheel of 100 slots, the first N slots are the "correct" wedge
        boolean correct = rng.nextInt(100) < level.correctPercent;

        int chosen = correct ? correctIndex : randomWrongIndex(correctIndex, optionCount, rng);

        long delay = level.minDelayMs + (long) (rng.nextDouble() * (level.maxDelayMs - level.minDelayMs));
        delay = Math.max(300, Math.min(delay, timeLimitMs - 300)); // never miss the buzzer by accident

        NpcAnswer answer = new NpcAnswer(correct, chosen, delay);
        pending = scheduler.schedule(() -> onAnswer.accept(answer), delay, TimeUnit.MILLISECONDS);
    }

    /** Call when the player answers first or the question ends, so the bot stops. */
    public synchronized void cancel() {
        if (pending != null) {
            pending.cancel(false);
            pending = null;
        }
    }

    /** Call when leaving the match screen. */
    public void shutdown() {
        cancel();
        scheduler.shutdownNow();
    }

    private static int randomWrongIndex(int correctIndex, int optionCount, ThreadLocalRandom rng) {
        if (optionCount <= 1) return correctIndex;
        int pick = rng.nextInt(optionCount - 1); // skip over the correct slot
        return pick >= correctIndex ? pick + 1 : pick;
    }
}
