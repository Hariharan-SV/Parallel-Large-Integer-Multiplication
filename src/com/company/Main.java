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
      if(x.length() == 0 || y.length() == 0) {
        this.result = "0";
        return;
      }
      if ((x.length() > 1 && y.length() == 1) || (x.length() == 1 && y.length() <= 2)) {
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

        KaratsubaMultiplication
           z0 = new KaratsubaMultiplication(x0, y0),
           z2 = new KaratsubaMultiplication(x1, y1),
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

        this.result = add(
           add(
              multiply(z2.result, String.valueOf(power(10, 2*m2))),
              multiply(z1 , String.valueOf(power(10, m2)))
           ), z0.result
        );
      }
    }
  }

  public static int max(int x, int y) {
    return (x>y)? x:y;
  }

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

  public static String add(String a, String b) {
    int i = a.length();
    int j = b.length();
    int k = Math.max(i, j) + 1;
    char[] c = new char[k];
    for (int digit = 0; k > 0; digit /= 10) {
      if (i > 0)
        digit += a.charAt(--i) - '0';
      if (j > 0)
        digit += b.charAt(--j) - '0';
      c[--k] = (char) ('0' + digit % 10);
    }
    for (k = 0; k < c.length - 1 && c[k] == '0'; k++) {}
    return new String(c, k, c.length - k);
  }

  static boolean isSmaller(String str1, String str2) {
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

  static String subtract(String str1, String str2) {
    if (isSmaller(str1, str2)) {
      String t = str1;
      str1 = str2;
      str2 = t;
    }

    String str = "";
    int n1 = str1.length(), n2 = str2.length();

    str1 = new StringBuilder(str1).reverse().toString();
    str2 = new StringBuilder(str2).reverse().toString();

    int carry = 0;

    for (int i = 0; i < n2; i++) {
      int sub
         = ((int)(str1.charAt(i) - '0')
         - (int)(str2.charAt(i) - '0') - carry);

      if (sub < 0) {
        sub = sub + 10;
        carry = 1;
      }
      else
        carry = 0;

      str += (char)(sub + '0');
    }

    for (int i = n2; i < n1; i++) {
      int sub = ((int)(str1.charAt(i) - '0') - carry);

      if (sub < 0) {
        sub = sub + 10;
        carry = 1;
      }
      else
        carry = 0;

      str += (char)(sub + '0');
    }
    return new StringBuilder(str).reverse().toString();
  }

  static String multiply(String num1, String num2)
  {
    int len1 = num1.length();
    int len2 = num2.length();
    if (len1 == 0 || len2 == 0)
      return "0";

    int result[] = new int[len1 + len2];

    int i_n1 = 0;
    int i_n2 = 0;

    for (int i = len1 - 1; i >= 0; i--) {
      int carry = 0;
      int n1 = num1.charAt(i) - '0';

      i_n2 = 0;

      for (int j = len2 - 1; j >= 0; j--) {
        int n2 = num2.charAt(j) - '0';
        int sum = n1 * n2 + result[i_n1 + i_n2] + carry;

        carry = sum / 10;
        result[i_n1 + i_n2] = sum % 10;
        i_n2++;
      }

      if (carry > 0)
        result[i_n1 + i_n2] += carry;

      i_n1++;
    }

    int i = result.length - 1;
    while (i >= 0 && result[i] == 0)
      i--;

    if (i == -1)
      return "0";

    String s = "";

    while (i >= 0)
      s += (result[i--]);

    return s;
  }


  public static String karatsuba(String x, String y) {
    if(x.length() == 0 || y.length() == 0) {
      return "0";
    }
    // Base case
    if ((x.length() <= 2 && y.length() == 1) || (x.length() == 1 && y.length() <= 2)) {
      return multiply(x,y);
    }
    // Recursive case:
    else {
      // Local variables
      long 	m = max(x.length(), y.length()),
         m2 = m/2;
      String[] num1 = strCopy(m2, x);
      String[] num2 = strCopy(m2, y);
      String
         x1 = num1[0],
         x0 = num1[1],
         y1 = num2[0],
         y0 = num2[1];

      String z0 = karatsuba(x0, y0),
         z2 = karatsuba(x1, y1),
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

  static String getNumericString(int n) {

    String numericString = "1234567890";
    StringBuilder sb = new StringBuilder(n);

    for (int i = 0; i < n; i++) {
      int index = (int)(numericString.length() * Math.random());
      sb.append(numericString.charAt(index));
    }

    return sb.toString();
  }


  public static void main(String[] args) throws InterruptedException {

    int n = 5,
        noOfProcessors = Runtime.getRuntime().availableProcessors();

    System.out.println("\nNo of processors : "+noOfProcessors+"\n\n");

    float [] averageSpeedups = new float[n];

    ForkJoinPool pool;

    long
       seqStartTime,
       seqEndTime,
       sequentialTimeElapsed,
       parStartTime,
       parEndTime,
       parallelTimeElapsed;

    for (int j = 1; j <= noOfProcessors; j++) {
      pool = new ForkJoinPool (j);

      for (int i = 0; i < n; i++) {
        seqStartTime = System.nanoTime();
        String x = getNumericString(18);
        String y = getNumericString(18);
        System.out.println(x+" x "+y+" = "+karatsuba(x,y));
        seqEndTime = System.nanoTime();
        sequentialTimeElapsed = seqEndTime - seqStartTime;
        System.out.println("Sequential Time : "+ sequentialTimeElapsed +" nano seconds");

        parStartTime = System.nanoTime();

        KaratsubaMultiplication task = new KaratsubaMultiplication(x,y);
        pool.invoke (task);
        parEndTime = System.nanoTime();
        parallelTimeElapsed = parEndTime - parStartTime;

        System.out.println("Parallel Time : "+ parallelTimeElapsed +" nano seconds");
        averageSpeedups[i] = (float)sequentialTimeElapsed/parallelTimeElapsed;
        System.out.println("Speedup : "+ averageSpeedups[i]+"\n");
      }

      float sum = 0;
      for (int i = 0; i < n; i++) {
        sum += averageSpeedups[i];
      }

      double average = sum / averageSpeedups.length;
      System.out.println("\n\nFor parallelism level:"+j+" , Average speedup : "+average+"\n\n");

      Thread.sleep(4500);
    }

  }

}