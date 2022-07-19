import java.util.*;

public class SubstringDivisibility {
    private static void insertionSort(ArrayList<Long> validPermList) {
        int end = validPermList.size();
        for (int i = 1; i < end; ++i) {
            long num = validPermList.get(i);
            int j = i - 1;

            while (j >= 0 && validPermList.get(j) > num) {
                validPermList.set(j + 1, validPermList.get(j));
                j = j - 1;
            }
            validPermList.set(j + 1, num);
        }
    }

    public static void main(String[] args) {
        long s = System.nanoTime();

        String input = args[0];
        int inputLength = args[0].length();
        int primesLimit = inputLength - 3;
        int[] primes = new int[] { 2, 3, 5, 7, 11, 13, 17 };
        long totalSum = 0;
        int inputBitSet = 0;
        ArrayList<Long> validPermList = new ArrayList<>();

        @SuppressWarnings("unchecked")
        ArrayList<long[]>[][] longMap = (ArrayList<long[]>[][]) new ArrayList[primesLimit][100];

        if (primesLimit < 1 || primesLimit > 7)
            throw new Error("Bad Input");

        for (int i = 0, len = input.length(); i < len; i++) {
            inputBitSet = inputBitSet | (1 << input.charAt(i) - 48);
        }

        for (int i = 0; i < primesLimit; i++) {
            int currentPrime = primes[i];
            int perm = i == 0 ? ((inputBitSet & 1) == 0 ? 124 : 12) : 0;

            threeDigitPermutations: for (; perm < 1000; perm = perm + currentPrime) {
                int modCheck = perm;
                int bits = 0;

                if (i == 0 && perm < 100 && (inputBitSet & 1) == 1)
                    bits = bits | 1;

                while (modCheck > 0) {
                    int lastDigit = modCheck % 10;
                    if ((inputBitSet & (1 << lastDigit)) == 0 || ((bits & (1 << lastDigit)) != 0))
                        continue threeDigitPermutations;

                    bits = bits | (1 << lastDigit);
                    modCheck = modCheck / 10;
                }

                int firstTwo = perm / 10;
                int lastTwo = perm % 100;
                int lastOne = perm % 10;

                if (i == 0) {
                    if (primesLimit != 1) {
                        if (longMap[0][lastTwo] == null)
                            longMap[0][lastTwo] = new ArrayList<>();

                        longMap[0][lastTwo].add(new long[] { (long) perm, (long) bits });
                        continue threeDigitPermutations;
                    }

                    long k = (long) (Math.log(inputBitSet ^ bits) / Math.log(2));
                    long firstDigit = k * (long) Math.pow(10, inputLength - 1);
                    validPermList.add(perm + firstDigit);
                    totalSum = totalSum + perm + firstDigit;
                    continue threeDigitPermutations;
                }

                if (longMap[i - 1][firstTwo] == null)
                    continue threeDigitPermutations;

                ArrayList<long[]> prevPrimeArr = longMap[i - 1][firstTwo];
                ArrayList<long[]> nextPrimeArr = longMap[i][lastTwo] == null
                        ? new ArrayList<>()
                        : longMap[i][lastTwo];

                makeNextPrimeArr: for (int j = 0, size = prevPrimeArr.size(); j < size; j++) {
                    long value = prevPrimeArr.get(j)[0];
                    long bitSet = prevPrimeArr.get(j)[1];

                    if ((bitSet & (1 << lastOne)) != 0)
                        continue makeNextPrimeArr;

                    if (i != primesLimit - 1) {
                        nextPrimeArr.add(new long[] { (value * 10) + lastOne, bitSet | (1 << lastOne) });
                        continue makeNextPrimeArr;
                    }

                    long validNum = (value * 10) + lastOne;
                    long latestBitSet = bitSet | (1 << lastOne);
                    long k = (long) (Math.log(inputBitSet ^ latestBitSet) / Math.log(2));
                    long firstDigit = k * (long) Math.pow(10, inputLength - 1);

                    validPermList.add(validNum + firstDigit);
                    totalSum = totalSum + validNum + firstDigit;
                }

                if (i != primesLimit - 1)
                    longMap[i][lastTwo] = nextPrimeArr;
            }
        }

        insertionSort(validPermList);

        long validPerm = 0;
        for (int i = 0, len = validPermList.size(); i < len; i++) {
            validPerm = validPermList.get(i);
            System.out.print((int) (Math.log10(validPerm) + 1) < inputLength ? "0" : "");
            System.out.println(validPerm);
        }

        System.out.print("Sum: ");
        System.out.println(totalSum);
        System.out.printf("Elapsed time: %.6f ms\n", (System.nanoTime() - s) / 1e6);
    }
}
