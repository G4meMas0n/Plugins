package de.g4memas0n.plugins.listener;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import java.util.Random;
import java.util.regex.Matcher;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The Regex Test-Suit that test the correct matching of the namespace regex.
 *
 * @author G4meMas0n
 * @since Release 2.0.0
 */
public class RegexMatcherTest {

    private final RandomGenerator generator;
    private final FilterListener listener;

    public RegexMatcherTest() {
        this.generator = new RandomGenerator();
        this.listener = new FilterListener();
    }

    @Test
    public void matchNamespacesTest() {
        int count = this.generator.randomCount(10, 100);

        while (count > 0) {
            final String command = "/" + this.generator.randomWord() + ":" + this.generator.randomAlphabetic();
            final Matcher match = this.listener.regex.matcher(command);

            assertTrue(String.format("Command '%s' do not match regex.", command), match.matches());
            assertNotNull(String.format("Command '%s' matches with missing namespace.", command), match.group("namespace"));
            assertNotNull(String.format("Command '%s' matches with missing command.", command), match.group("command"));

            count--;
        }
    }

    /**
     * The random generator that generates random alphabetic and alphanumeric words.
     *
     * @author G4meMas0n
     * @since Release 2.0.0
     */
    public static class RandomGenerator {

        private static final int START_ALPHABETIC = 65;
        private static final int START_ALPHANUMERIC = 48;
        private static final int END = 123;

        private final Random random;

        public RandomGenerator() {
            this.random = new Random();
        }

        @SuppressWarnings("unused")
        public int randomCount() {
            return this.random.nextInt();
        }

        public int randomCount(final int begin, final int bound) {
            return this.random.nextInt(bound - begin) + begin;
        }

        public @NotNull String randomAlphabetic() {
            return this.randomAlphabetic(this.random.nextInt(13) + 3);
        }

        public @NotNull String randomAlphabetic(final int length) {
            return this.random.ints(START_ALPHABETIC, END).filter(code -> code <= 90 || code >= 97).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        }

        @SuppressWarnings("unused")
        public @NotNull String randomAlphanumeric() {
            return this.randomAlphanumeric(this.random.nextInt(13) + 3);
        }

        public @NotNull String randomAlphanumeric(final int length) {
            return this.random.ints(START_ALPHANUMERIC, END)
                    .filter(code -> code <= 57 || code >= 97 || (code >= 65 && code <= 90)).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        }

        public @NotNull String randomWord() {
            return this.randomWord(this.random.nextInt(13) + 3);
        }

        public @NotNull String randomWord(final int length) {
            return this.random.ints(START_ALPHANUMERIC, END)
                    .filter(code -> code <= 57 || code >= 97 || (code >= 65 && code <= 90) || code == 95).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        }
    }
}
