package com.company;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class Main {

  private static class KaratsubaMultiplication extends RecursiveAction {
    private final String num1;
    private final String num2;
    public String result;
    KaratsubaMultiplication(String num11, String num21) {
      this.num1 = num11;
      this.num2 = num21;
    }

    @Override
    protected void compute() {
      String x = this.num1;
      String y = this.num2;
      if ((x.length() <= 2 && y.length() == 1) || (x.length() == 1 && y.length() <= 2)) {
        this.result = multiply(x,y);
        return;
      }
      else {
        long
           m = max(x.length(), y.length()),
           m2 = m/2;
        String[]
           num1 = strCopy(m2, x),
           num2 = strCopy(m2, y);
        String
           x1 = num1[0],
           x0 = num1[1],
           y1 = num2[0],
           y0 = num2[1];

        //System.out.println("X = "+x+" {"+x1+ " " +x0 + "}, Y = "+y+" {" + y1 + " " +y0+"};");

        KaratsubaMultiplication
           z0 = new KaratsubaMultiplication(x0, y0), // z0 = x0y0
           z2 = new KaratsubaMultiplication(x1, y1), // z2 = x1y1
           z1Temp = new KaratsubaMultiplication(
              add(x0, x1),
              add(y0 ,y1)
           );
        invokeAll(z0,z2,z1Temp);
        String z1 = subtract(
              subtract(
                 z1Temp.result
                 , z2.result)
              , z0.result);
        // z1 = (x0 + x1)*(y1 + y0) - z2 - z0
//            return (z2 * power(10, 2*m2) + (z1 * power(10, m2)) + z0);
        this.result = add(
           add(
              multiply(z2.result, String.valueOf(power(10, 2*m2))),
              multiply(z1 , String.valueOf(power(10, m2)))
           ), z0.result
        );
      }
    }
  }

  // Takes two integers and returns the maximum of them
  public static int max(int x, int y) {
    return (x>y)? x:y;
  }

  // Takes a string and an index.
  // The index in this case is the "m". It will count backwards from the last (least significant) digit and split the string there.
  // It will return a 2-element array of the split string.
  // For example: Given 12345 as the string and 2 as the index, it will split the string into the string array ["123", "45"].
  // This is so the 123 can be written as 123 * 10^m, with m = 2 the index.
  public static String[] strCopy(long index, String string) {
    String	first = "",
       last = "";
    long actualIndex = string.length() - index;
    for (int i = 0; i<actualIndex; i++) {
      first+=string.charAt(i);
    }
    for (int i = (int)actualIndex; i<string.length(); i++) {
      last+=string.charAt(i);
    }
    return new String[] {first, last};
  }

  // An exponent function. Works the same way as Math.pow, but with 64bit integers instead of double precision floats.
  public static long power(long x, long y) {
    if (y == 0)
      return 1;
    else {
      long answer = 1;
      for (int i = 1; i<=y; i++) {
        answer *= x;
      }
      return answer;
    }
  }

  /*
   * Take two numbers, x and y.
   * Example: 12345 and 6789.
   * Find a base b and power m to separate it into.
   * We'll pick base = 10, and m to be half the length of the digits of the numbers in this implementation of the algorithm.
   * 	In this case, m will be 2, so 10^2 = 100. We will split the 2 numbers using this multiplier.
   * The form we want is:
   * x = x1*b^m + x0
   * y = y1*b^m + y0
   * ----------
   * Using the above example,
   * x1 = 123
   * x0 = 45
   * ----------
   * y1 = 67
   * y2 = 89
   * ----------
   * b = 10 and m = 2
   * ----------
   * Thus:
   * 12345 = 123 * 10^2  +  45
   * 6789 =   67 * 10^2  +  89
   *
   *
   * The recursive algorithm is as follows:
   *
   * If x<10 or y<10, return x*y. Single digit multiplication is the base case.
   * Otherwise:
   * Let z2 = karatsuba(x1, y1). x1 and y1 are the most significant digits, and are the local variables "high".
   * Let z0 = karatsuba(x0, y0). x0 and y0 are the least significant digits, and are the local variables "low".
   * Let z1 = karatsuba(x1+y0, x0+y1) - z0 - z2.
   * And the result is the following sum:
   * z2 * b^2m	+	z1 * b^m	+	z0
   *
   * @param x The multiplicand.
   * @param y The multiplier.
   * @return The product.
   */

  public static String add(String a, String b) {
    int i = a.length();
    int j = b.length();
    int k = Math.max(i, j) + 1; // room for carryover
    char[] c = new char[k];
    for (int digit = 0; k > 0; digit /= 10) {
      if (i > 0)
        digit += a.charAt(--i) - '0';
      if (j > 0)
        digit += b.charAt(--j) - '0';
      c[--k] = (char) ('0' + digit % 10);
    }
    for (k = 0; k < c.length - 1 && c[k] == '0'; k++) {/*Skip leading zeroes*/}
    return new String(c, k, c.length - k);
  }

  // Returns true if str1 is smaller than str2.
  static boolean isSmaller(String str1, String str2)
  {
    // Calculate lengths of both string
    int n1 = str1.length(), n2 = str2.length();
    if (n1 < n2)
      return true;
    if (n2 < n1)
      return false;

    for (int i = 0; i < n1; i++)
      if (str1.charAt(i) < str2.charAt(i))
        return true;
      else if (str1.charAt(i) > str2.charAt(i))
        return false;

    return false;
  }

  // Function for find difference of larger numbers
  static String subtract(String str1, String str2)
  {
    // Before proceeding further, make sure str1
    // is not smaller
    if (isSmaller(str1, str2)) {
      String t = str1;
      str1 = str2;
      str2 = t;
    }

    // Take an empty string for storing result
    String str = "";

    // Calculate length of both string
    int n1 = str1.length(), n2 = str2.length();

    // Reverse both of strings
    str1 = new StringBuilder(str1).reverse().toString();
    str2 = new StringBuilder(str2).reverse().toString();

    int carry = 0;

    // Run loop till small string length
    // and subtract digit of str1 to str2
    for (int i = 0; i < n2; i++) {
      // Do school mathematics, compute difference of
      // current digits
      int sub
         = ((int)(str1.charAt(i) - '0')
         - (int)(str2.charAt(i) - '0') - carry);

      // If subtraction is less then zero
      // we add then we add 10 into sub and
      // take carry as 1 for calculating next step
      if (sub < 0) {
        sub = sub + 10;
        carry = 1;
      }
      else
        carry = 0;

      str += (char)(sub + '0');
    }

    // subtract remaining digits of larger number
    for (int i = n2; i < n1; i++) {
      int sub = ((int)(str1.charAt(i) - '0') - carry);

      // if the sub value is -ve, then make it
      // positive
      if (sub < 0) {
        sub = sub + 10;
        carry = 1;
      }
      else
        carry = 0;

      str += (char)(sub + '0');
    }

    // reverse resultant string
    return new StringBuilder(str).reverse().toString();
  }

  static String multiply(String num1, String num2)
  {
    int len1 = num1.length();
    int len2 = num2.length();
    if (len1 == 0 || len2 == 0)
      return "0";

    // will keep the result number in vector
    // in reverse order
    int result[] = new int[len1 + len2];

    // Below two indexes are used to
    // find positions in result.
    int i_n1 = 0;
    int i_n2 = 0;

    // Go from right to left in num1
    for (int i = len1 - 1; i >= 0; i--)
    {
      int carry = 0;
      int n1 = num1.charAt(i) - '0';

      // To shift position to left after every
      // multipliccharAtion of a digit in num2
      i_n2 = 0;

      // Go from right to left in num2
      for (int j = len2 - 1; j >= 0; j--)
      {
        // Take current digit of second number
        int n2 = num2.charAt(j) - '0';

        // Multiply with current digit of first number
        // and add result to previously stored result
        // charAt current position.
        int sum = n1 * n2 + result[i_n1 + i_n2] + carry;

        // Carry for next itercharAtion
        carry = sum / 10;

        // Store result
        result[i_n1 + i_n2] = sum % 10;

        i_n2++;
      }

      // store carry in next cell
      if (carry > 0)
        result[i_n1 + i_n2] += carry;

      // To shift position to left after every
      // multipliccharAtion of a digit in num1.
      i_n1++;
    }

    // ignore '0's from the right
    int i = result.length - 1;
    while (i >= 0 && result[i] == 0)
      i--;

    // If all were '0's - means either both
    // or one of num1 or num2 were '0'
    if (i == -1)
      return "0";

    // genercharAte the result String
    String s = "";

    while (i >= 0)
      s += (result[i--]);

    return s;
  }


  public static String karatsuba(String x, String y) {
    // Base case
    if ((x.length() <= 2 && y.length() == 1) || (x.length() == 1 && y.length() <= 2)) {
      return multiply(x,y);
    }
    // Recursive case:
    // Decompose the problem by splitting the integers and applying the algorithm on the parts.
    else {
      // Local variables
      long 	m = max(x.length(), y.length()), // the maximum # of digits
         m2 = m/2; // the middle; if the number is odd, it will floor the fraction
      String[] num1 = strCopy(m2, x);
      String[] num2 = strCopy(m2, y);
      String
         x1 = num1[0],
         x0 = num1[1],
         y1 = num2[0],
         y0 = num2[1];

      String z0 = karatsuba(x0, y0),  // z0 = x0y0
         z2 = karatsuba(x1, y1),      // z2 = x1y1
         z1 = subtract(
            subtract(
               karatsuba(
                  add(x0, x1),
                  add(y0 ,y1)
               )
               , z2)
            , z0);

      return
         add(
            add(
               multiply(z2, String.valueOf(power(10, 2*m2))),
               multiply(z1 , String.valueOf(power(10, m2)))
            ), z0
         );
    }
  }

  public static void main(String[] args) {

    long start = System.currentTimeMillis();
    String x = "123123123123123123";
    String y = "122123123123123123";
    System.out.println(karatsuba(x,y));
    long end = System.currentTimeMillis();
    long timeElapsed = end-start;
    System.out.println("Time : "+ timeElapsed +" milliseconds");

    start = System.currentTimeMillis();
    ForkJoinPool pool = new ForkJoinPool ();
    KaratsubaMultiplication task = new KaratsubaMultiplication(x,y);
    pool.invoke (task);
    end = System.currentTimeMillis();
    timeElapsed = end-start;
    System.out.println(task.result);
    System.out.println("Time : "+ timeElapsed +" milliseconds");

  }

}