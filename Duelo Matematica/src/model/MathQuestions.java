package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MATH QUESTIONS — the question bank (your data, packaged).
 *
 * The raw rows live in one private static String[][] — grouped by
 * category with comments — and the public methods turn those rows into
 * Question objects on demand.
 *
 * ROW LAYOUT:
 *   [0] id           e.g. "1"
 *   [1] topic        "Calculus 1" / "Statistics" / "Symbolic Logic" / …
 *   [2] difficulty   "Easy" / "Medium" / "Hard" / "Very Hard"
 *   [3] prompt       may contain $...$ LaTeX
 *   [4] correct      may contain $...$ LaTeX
 *   [5] wrong 1
 *   [6] wrong 2
 *   [7] wrong 3
 *
 * WHY A STATIC ARRAY? The bank never changes at runtime. Loading once
 * from compiled Java code is simpler and faster than a file or DB.
 */
public final class MathQuestions {

    private MathQuestions() { }   // utility container — never instantiated

    private static final String[][] RAW = {
        // ==========================================
        // CALCULUS 1 (10 Questions)
        // ==========================================
        { "1", "Calculus 1", "Easy",
          "What is the derivative of $f(x) = x^5 - 3x^2 + 1$?",
          "$5x^4 - 6x$", "$5x^4 - 6x + 1$", "$x^4 - 6x$", "$5x^4 - 3x$" },
        { "2", "Calculus 1", "Easy",
          "Evaluate the limit: $\\lim_{x \\to 3} \\frac{x^2 - 9}{x - 3}$.",
          "$0$", "$3$", "$6$", "Undefined" },
        { "3", "Calculus 1", "Easy",
          "What is the derivative of $f(x) = \\cos(4x)$?",
          "$-4\\sin(4x)$", "$4\\sin(4x)$", "$-\\sin(4x)$", "$-4\\cos(4x)$" },
        { "4", "Calculus 1", "Easy",
          "Evaluate the limit: $\\lim_{x \\to 0} \\frac{e^x - 1}{x}$.",
          "$1$", "$0$", "$\\infty$", "$e$" },
        { "5", "Calculus 1", "Medium",
          "Find the critical points of $f(x) = x^3 - 6x^2 + 9x$.",
          "$x = 1$ and $x = 3$", "$x = 0$ and $x = 3$",
          "$x = -1$ and $x = -3$", "$x = 2$ and $x = 4$" },
        { "6", "Calculus 1", "Medium",
          "What is the equation of the line tangent to $y = \\ln(x)$ at $x = 1$?",
          "$y = x - 1$", "$y = x$", "$y = x + 1$", "$y = 1 - x$" },
        { "7", "Calculus 1", "Medium",
          "Using implicit differentiation on $x^2 + y^2 = 25$, what is $\\frac{dy}{dx}$?",
          "$-\\frac{x}{y}$", "$-\\frac{y}{x}$", "$\\frac{x}{y}$", "$\\frac{2x}{y}$" },
        { "8", "Calculus 1", "Hard",
          "Evaluate the limit of indeterminate form: $\\lim_{x \\to 0} (1 + 2x)^{\\frac{1}{x}}$.",
          "$e^2$", "$e$", "$1$", "$\\infty$" },
        { "9", "Calculus 1", "Hard",
          "If a sphere's radius grows at $2\\text{ cm/s}$, at what rate is the volume increasing when $r = 3\\text{ cm}$?",
          "$72\\pi \\text{ cm}^3/\\text{s}$", "$36\\pi \\text{ cm}^3/\\text{s}$",
          "$108\\pi \\text{ cm}^3/\\text{s}$", "$18\\pi \\text{ cm}^3/\\text{s}$" },
        { "10", "Calculus 1", "Very Hard",
          "Evaluate the limit: $\\lim_{x \\to 0} \\frac{\\sin(x) - x + \\frac{x^3}{6}}{x^5}$.",
          "$\\frac{1}{120}$", "$0$", "$\\frac{1}{24}$", "$\\frac{1}{6}$" },

        // ==========================================
        // CALCULUS 2 (10 Questions)
        // ==========================================
        { "1", "Calculus 2", "Easy",
          "Evaluate the indefinite integral $\\int x^3 \\, dx$.",
          "$\\frac{1}{4}x^4 + C$", "$3x^2 + C$", "$\\frac{1}{3}x^4 + C$", "$x^4 + C$" },
        { "2", "Calculus 2", "Easy",
          "Evaluate $\\int e^{5x} \\, dx$.",
          "$\\frac{1}{5}e^{5x} + C$", "$5e^{5x} + C$", "$e^{5x} + C$", "$\\frac{1}{25}e^{5x} + C$" },
        { "3", "Calculus 2", "Easy",
          "Evaluate $\\int \\frac{1}{1 + x^2} \\, dx$.",
          "$\\arctan(x) + C$", "$\\ln(1+x^2) + C$", "$\\arcsin(x) + C$", "$\\frac{1}{2x} + C$" },
        { "4", "Calculus 2", "Easy",
          "What is the radius of convergence $R$ for the geometric series $\\sum_{n=0}^{\\infty} x^n$?",
          "$R = 1$", "$R = \\infty$", "$R = 0$", "$R = 2$" },
        { "5", "Calculus 2", "Medium",
          "Using integration by parts, evaluate $\\int x e^x \\, dx$.",
          "$x e^x - e^x + C$", "$x e^x + e^x + C$",
          "$\\frac{1}{2}x^2 e^x + C$", "$e^x - x e^x + C$" },
        { "6", "Calculus 2", "Medium",
          "Evaluate $\\int \\frac{1}{x^2 - 1} \\, dx$ using partial fractions.",
          "$\\frac{1}{2}\\ln\\left|\\frac{x-1}{x+1}\\right| + C$",
          "$\\ln|x^2 - 1| + C$", "$\\arctan(x) + C$", "$\\frac{1}{2}\\ln|x^2 - 1| + C$" },
        { "7", "Calculus 2", "Medium",
          "What is the Maclaurin series expansion for $\\cos(x)$?",
          "$\\sum_{n=0}^{\\infty} (-1)^n \\frac{x^{2n}}{(2n)!}$",
          "$\\sum_{n=0}^{\\infty} (-1)^n \\frac{x^{2n+1}}{(2n+1)!}$",
          "$\\sum_{n=0}^{\\infty} \\frac{x^n}{n!}$",
          "$\\sum_{n=0}^{\\infty} (-1)^n x^{2n}$" },
        { "8", "Calculus 2", "Hard",
          "Compute the arc length of $y = \\frac{2}{3}x^{3/2}$ from $x = 0$ to $x = 3$.",
          "$\\frac{14}{3}$", "$\\frac{16}{3}$", "$4$", "$\\frac{8}{3}$" },
        { "9", "Calculus 2", "Hard",
          "Determine the behavior of the alternating series $\\sum_{n=2}^{\\infty} \\frac{(-1)^n}{\\ln(n)}$.",
          "Conditionally Convergent", "Absolutely Convergent",
          "Divergent", "Oscillating without bound" },
        { "10", "Calculus 2", "Very Hard",
          "Evaluate the improper integral $\\int_0^{\\infty} x^3 e^{-x} \\, dx$.",
          "$6$", "$24$", "$3$", "Diverges" },

        // ==========================================
        // CALCULUS 3 (10 Questions)
        // ==========================================
        { "1", "Calculus 3", "Easy",
          "Compute the dot product of $\\mathbf{u} = \\langle 2, 3 \\rangle$ and $\\mathbf{v} = \\langle -1, 4 \\rangle$.",
          "$10$", "$14$", "$-2$", "$12$" },
        { "2", "Calculus 3", "Easy",
          "Find the partial derivative $\\frac{\\partial f}{\\partial y}$ for $f(x, y) = x^3 + y^3 - 3xy$.",
          "$3y^2 - 3x$", "$3x^2 - 3y$", "$3y^2$", "$3y^2 - 3$" },
        { "3", "Calculus 3", "Easy",
          "What is the gradient vector $\\nabla f$ for $f(x, y) = x^2 y$?",
          "$\\langle 2xy, x^2 \\rangle$", "$\\langle x^2, 2xy \\rangle$",
          "$2xy + x^2$", "$\\langle 2x, 1 \\rangle$" },
        { "4", "Calculus 3", "Medium",
          "Compute the cross product $\\langle 1, 0, 0 \\rangle \\times \\langle 0, 1, 0 \\rangle$.",
          "$\\langle 0, 0, 1 \\rangle$", "$\\langle 0, 0, -1 \\rangle$",
          "$0$", "$\\langle 1, 1, 0 \\rangle$" },
        { "5", "Calculus 3", "Medium",
          "Find the divergence $\\nabla \\cdot \\mathbf{F}$ of $\\mathbf{F} = \\langle x^2, y^2, z^2 \\rangle$.",
          "$2x + 2y + 2z$", "$\\langle 2x, 2y, 2z \\rangle$",
          "$x + y + z$", "$0$" },
        { "6", "Calculus 3", "Medium",
          "What is the directional derivative $D_{\\mathbf{u}}f$ in terms of $\\nabla f$ and unit vector $\\mathbf{u}$?",
          "$\\nabla f \\cdot \\mathbf{u}$", "$\\nabla f \\times \\mathbf{u}$",
          "$|\\nabla f| |\\mathbf{u}|$", "$\\frac{\\nabla f}{|\\mathbf{u}|}$" },
        { "7", "Calculus 3", "Medium",
          "What is the area element $dA$ in polar coordinates?",
          "$r \\, dr \\, d\\theta$", "$dr \\, d\\theta$",
          "$r^2 \\, dr \\, d\\theta$", "$\\frac{1}{r} \\, dr \\, d\\theta$" },
        { "8", "Calculus 3", "Hard",
          "Compute the curl $\\nabla \\times \\mathbf{F}$ of the conservative field $\\mathbf{F}(x,y,z) = \\langle yz, xz, xy \\rangle$.",
          "$\\langle 0, 0, 0 \\rangle$", "$\\langle z, z, y \\rangle$",
          "$\\langle 1, 1, 1 \\rangle$", "$x + y + z$" },
        { "9", "Calculus 3", "Hard",
          "State Green's Theorem formula for a line integral over closed region $D$ bounded by $C$.",
          "$\\iint_D \\left(\\frac{\\partial Q}{\\partial x} - \\frac{\\partial P}{\\partial y}\\right) dA$",
          "$\\iint_D \\left(\\frac{\\partial P}{\\partial x} + \\frac{\\partial Q}{\\partial y}\\right) dA$",
          "$\\iiint_V (P + Q) \\, dV$", "$\\oint_C (P + Q) \\, ds$" },
        { "10", "Calculus 3", "Very Hard",
          "Using the Divergence Theorem, evaluate $\\iint_S \\mathbf{F} \\cdot d\\mathbf{S}$ where $\\mathbf{F} = \\langle x, y, z \\rangle$ over the unit sphere $x^2 + y^2 + z^2 = 1$.",
          "$4\\pi$", "$\\frac{4}{3}\\pi$", "$2\\pi$", "$0$" },

        // ==========================================
        // STATISTICS (10 Questions)
        // ==========================================
        { "1", "Statistics", "Easy",
          "What is the mean of the dataset $\\{2, 4, 6, 8, 10\\}$?",
          "$6$", "$5$", "$7$", "$30$" },
        { "2", "Statistics", "Easy",
          "Which measure of central tendency is calculated as the sum of all values divided by total count?",
          "Mean", "Median", "Mode", "Variance" },
        { "3", "Statistics", "Easy",
          "What is the probability of rolling an even number on a fair six-sided die?",
          "$\\frac{1}{2}$", "$\\frac{1}{6}$", "$\\frac{1}{3}$", "$\\frac{2}{3}$" },
        { "4", "Statistics", "Easy",
          "Formula for standardizing a raw score $x$ into a $z$-score with mean $\\mu$ and std dev $\\sigma$:",
          "$z = \\frac{x - \\mu}{\\sigma}$", "$z = \\frac{\\mu - x}{\\sigma}$",
          "$z = \\frac{x - \\sigma}{\\mu}$", "$z = (x - \\mu)\\sigma$" },
        { "5", "Statistics", "Medium",
          "What is the expected value $E[X]$ of a Binomial random variable $X \\sim \\text{Binom}(n, p)$?",
          "$np$", "$np(1-p)$", "$\\sqrt{np(1-p)}$", "$\\frac{p}{n}$" },
        { "6", "Statistics", "Medium",
          "The Central Limit Theorem states that as sample size $n$ increases, the sample mean approaches what distribution?",
          "Normal Distribution", "Uniform Distribution",
          "Poisson Distribution", "Binomial Distribution" },
        { "7", "Statistics", "Medium",
          "In hypothesis testing, what is a Type I error?",
          "Rejecting $H_0$ when $H_0$ is true",
          "Failing to reject $H_0$ when $H_0$ is false",
          "Accepting $H_0$ when $H_0$ is false",
          "Rejecting $H_1$ when $H_1$ is true" },
        { "8", "Statistics", "Hard",
          "What is the variance of a Continuous Uniform Distribution $U(a, b)$?",
          "$\\frac{(b - a)^2}{12}$", "$\\frac{b - a}{2}$",
          "$\\frac{(b - a)^2}{2}$", "$\\frac{b + a}{12}$" },
        { "9", "Statistics", "Hard",
          "According to Bayes' Theorem, what is $P(A|B)$ equal to?",
          "$\\frac{P(B|A)P(A)}{P(B)}$", "$\\frac{P(A|B)P(B)}{P(A)}$",
          "$\\frac{P(A \\cap B)}{P(A)}$", "$P(A)P(B)$" },
        { "10", "Statistics", "Very Hard",
          "What is the Moment Generating Function (MGF) $M_Z(t)$ of a standard normal variable $Z \\sim \\mathcal{N}(0, 1)$?",
          "$e^{\\frac{t^2}{2}}$", "$e^{t}$", "$\\frac{1}{1 - t}$",
          "$e^{\\mu t + \\frac{1}{2}\\sigma^2 t^2}$" },

        // ==========================================
        // SYMBOLIC LOGIC (10 Questions)
        // ==========================================
        { "1", "Symbolic Logic", "Easy",
          "What is the truth value of $P \\land Q$ when $P = \\text{True}$ and $Q = \\text{False}$?",
          "False", "True", "Undefined", "Tautology" },
        { "2", "Symbolic Logic", "Easy",
          "What is the contrapositive of $P \\rightarrow Q$?",
          "$\\neg Q \\rightarrow \\neg P$", "$Q \\rightarrow P$",
          "$\\neg P \\rightarrow \\neg Q$", "$P \\land \\neg Q$" },
        { "3", "Symbolic Logic", "Easy",
          "A statement that is true under every possible interpretation is called a:",
          "Tautology", "Contradiction", "Contingency", "Fallacy" },
        { "4", "Symbolic Logic", "Medium",
          "According to De Morgan's Laws, $\\neg(P \\lor Q)$ is equivalent to:",
          "$\\neg P \\land \\neg Q$", "$\\neg P \\lor \\neg Q$",
          "$P \\land Q$", "$\\neg P \\rightarrow \\neg Q$" },
        { "5", "Symbolic Logic", "Medium",
          "Which inference rule states: Given $P$ and $P \\rightarrow Q$, we can infer $Q$?",
          "Modus Ponens", "Modus Tollens",
          "Hypothetical Syllogism", "Disjunctive Syllogism" },
        { "6", "Symbolic Logic", "Medium",
          "Which logical expression is logically equivalent to $P \\rightarrow Q$?",
          "$\\neg P \\lor Q$", "$\\neg P \\land Q$",
          "$P \\land \\neg Q$", "$P \\lor Q$" },
        { "7", "Symbolic Logic", "Medium",
          "What is the logical negation of $\\forall x \\, P(x)$?",
          "$\\exists x \\, \\neg P(x)$", "$\\forall x \\, \\neg P(x)$",
          "$\\neg \\exists x \\, P(x)$", "$\\exists x \\, P(x)$" },
        { "8", "Symbolic Logic", "Hard",
          "Which inference rule states: Given $P \\rightarrow Q$ and $Q \\rightarrow R$, we can infer $P \\rightarrow R$?",
          "Hypothetical Syllogism", "Modus Ponens",
          "Disjunctive Syllogism", "Resolution" },
        { "9", "Symbolic Logic", "Hard",
          "Which logical equivalence holds for the biconditional $P \\leftrightarrow Q$?",
          "$(P \\rightarrow Q) \\land (Q \\rightarrow P)$",
          "$(P \\lor Q) \\land (\\neg P \\lor \\neg Q)$",
          "$(P \\rightarrow Q) \\lor (Q \\rightarrow P)$",
          "$\\neg P \\leftrightarrow \\neg Q$" },
        { "10", "Symbolic Logic", "Very Hard",
          "What is the logical negation of $\\forall x \\, \\exists y \\, (P(x,y) \\rightarrow Q(x,y))$?",
          "$\\exists x \\, \\forall y \\, (P(x,y) \\land \\neg Q(x,y))$",
          "$\\exists x \\, \\forall y \\, (P(x,y) \\rightarrow \\neg Q(x,y))$",
          "$\\exists x \\, \\forall y \\, (\\neg P(x,y) \\land Q(x,y))$",
          "$\\forall x \\, \\exists y \\, (P(x,y) \\land \\neg Q(x,y))$" }
    };

    /**
     * Turn every row into a Question object. Called once when a game
     * starts. Returns a fresh list each time so GameService can shuffle
     * without touching the source data.
     */
    public static List<Question> loadAll() {
        List<Question> out = new ArrayList<>(RAW.length);
        for (String[] row : RAW) {
            out.add(new Question(row[0], row[1], row[2], row[3],
                                 row[4], row[5], row[6], row[7]));
        }
        return out;
    }

    /** Distinct topics present in the bank — useful for tests / future UIs. */
    public static Set<String> getTopics() {
        Set<String> topics = new HashSet<>();
        for (String[] row : RAW) topics.add(row[1]);
        return topics;
    }
}