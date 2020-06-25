import java.util.*;
import java.lang.*;
import java.io.*;

public class GeneticAlgorithm {

    public static int[] jukhapdo(int[][] hubohae) {
        // y=ax+b 형태의 식 구현하기
        int b = 117; // 0살(미국 나이)인 인구가 117임을 이용하여 b는 117로 고정
        int[] jhd_sum = new int[10]; // 각 기울기에서 적합도의 총합

        for (int a = 0; a < 10; a++) {
            jhd_sum[a] = 0;
            // System.out.println("기울기가 " + a + "일 때");
            for (int x = 0; x < hubohae.length; x++) {
                int jhd_gap = 0;
                int yesang = (a * hubohae[x][0]) + b;
                int chai = hubohae[x][1] - yesang;
                jhd_gap = (chai * chai); // 각 예상 기울기 값의 적합도 계산 (MSE 계산)
                // System.out.println("후보해 " + x + "의 적합도는 " + jhd_gap + "이다.");
                jhd_sum[a] += jhd_gap;
            }
            // System.out.println("기울기 " + a + "의 총 적합도는 " + jhd_sum[a] + "이다.");
        }

        int min_jhd = jhd_sum[0];
        int max_jhd = jhd_sum[0];
        for (int i = 0; i < 10; i++) {
            if (jhd_sum[i] < min_jhd) min_jhd = jhd_sum[i]; // 가장 작은 적합도 값 찾기
            if (jhd_sum[i] > max_jhd) max_jhd = jhd_sum[i]; // 가장 큰 적합도 값 찾기
        }

        // System.out.println("통계자료와 가장 유사한 값을 가지는 적합도는 "+min_jhd+"이다.");
        // System.out.println("통계자료와 가장 다른 값을 가지는 적합도는 "+max_jhd+"이다.");

        int max = max_jhd;

        int[] edited_jhd_sum = new int [10];
        for (int j = 0; j < 10; j++) {
            edited_jhd_sum[j] = min_jhd + max_jhd - jhd_sum[j]; // 적합도가 작은 값이 큰 적합도를 갖도록 수정
            // System.out.println("기울기가 " + j + "일 때 적합도는 " + edited_jhd_sum[j][0] + "가 된다.");
        }

        return edited_jhd_sum;
    }

    public static int[] selection(int[] jhd) {
        int sum = 0;
        int[] f = new int[10];
        for (int i = 0; i < 10; i++) {
            f[i] = jhd[i]; // 계산된 적합도를 f[i]에 넣음
            sum += f[i]; // 각 기울기의 적합도를 sum에 더함
        }

        double[] ratio = new double[10];
        for (int i = 0; i < 10; i++) {
            if (i == 0) ratio[i] = (double) f[i] / (double) sum; // 적합도에 비례하도록 면적을 할당함
            else ratio[i] = ratio[i - 1] + ((double) f[i] / (double) sum); // 총 합이 1이 되도록 원반을 만듦
        }

        int[] sx = new int[10];
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            double pin = random.nextDouble(); // 0~1 사이의 임의의 값 선택
            if (pin < ratio[0]) sx[i] = 0;
            else if (pin < ratio[1]) sx[i] = 1;
            else if (pin < ratio[2]) sx[i] = 2;
            else if (pin < ratio[3]) sx[i] = 3;
            else if (pin < ratio[4]) sx[i] = 4;
            else if (pin < ratio[5]) sx[i] = 5;
            else if (pin < ratio[6]) sx[i] = 6;
            else if (pin < ratio[7]) sx[i] = 7;
            else if (pin < ratio[8]) sx[i] = 8;
            else sx[i] = 9; // 원반의 핀이 가리킨 범위 내의 후보해(기울기) 선택
        }

        for(int i = 0 ; i <10; i++) {
            // System.out.printf("%d ", sx[i]);
        } // System.out.println();

        return sx;
    }

    public static String int2String(String x) {
        return String.format("%4s", x).replace(' ', '0');
    }

    public static String[] crossOver(int[] sx) {
        String[] hubohae_co = new String[sx.length];
        for (int i = 0; i < sx.length; i += 2) {
            String bit1 = int2String(Integer.toBinaryString(sx[i]));
            String bit2 = int2String(Integer.toBinaryString(sx[i + 1]));
            // 2개의 후보해를 이진수로 변환
            // System.out.println(sx[i] + "의 이진수 표현은 " + bit1 + "이다.");
            // System.out.println(sx[i+1] + "의 이진수 표현은 " + bit2 + "이다.");

            hubohae_co[i] = bit1.substring(0, 2) + bit2.substring(2, 4);
            hubohae_co[i + 1] = bit2.substring(0, 2) + bit1.substring(2, 4);
            // 두 이진수의 절반 부분을 서로 교환
            // System.out.println(bit1 + "와 " + bit2 + "를 교차 연산시");
            // System.out.println(hubohae_co[i] + "와 " + hubohae_co[i+1] + "가 된다.");
        }

        return hubohae_co;
    }

    public static int[] mutation(String[] cx) {
        Random random = new Random();
        int[] binary_decimal = new int [cx.length];
        for (int i = 0; i < cx.length; i++) {
            binary_decimal[i] = Integer.parseInt(cx[i],2); // 2진수를 10진수로 변환
            // System.out.println(cx[i] + " -> " + binary_decimal[i]);
        }

        for (int i = 0; i < binary_decimal.length; i++) {
            double pin = (double)1 / (double)16;
            if (random.nextDouble() < pin) {
                binary_decimal[i] = (1 << i/8) ^ binary_decimal[i]; // 1을 i/4 의 몫 만큼 비트 이동한 후에 a와 XOR 연산
                // System.out.println(i + "의 돌연변이 값은 "
                //         + int2String(Integer.toBinaryString(binary_decimal[i]))
                //         + "(" + binary_decimal[i] + ")");
            }
        }

        return binary_decimal;
    }

    public static int count(int[] mx) {
        int[] count = new int[1000];
        for (int i = 0; i < mx.length; i++) {
            count[mx[i]]++; // 해당 기울기 값이 나오면 그 횟수 만큼 count 배열에 저장
        }

        int gradient = 0; // 가장 많이 나온 기울기 값
        int frequency = 0; // 가장 많이 나온 기울기 값 나온 횟수
        for (int j = 0; j < count.length; j++) {
            if (frequency < count[j]) {
                frequency = count[j];
                gradient = j; // 가장 많이 나온 기울기 값 선정
            }
        }

        return gradient;
    }

    public static void main(String[] args) {
        ArrayList<Integer> age_al = new ArrayList<>();
        ArrayList<Integer> pop_al = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("Population.csv"));
            // https://data.seoul.go.kr/dataList/421/S/2/datasetView.do
            // 각 나이에 대한 인구의 수가 들어있는 Population.csv 파일을 불러옴
            String line;
            while (!((line = br.readLine()) == null)) {
                String[] a = line.split(",");
                age_al.add(Integer.parseInt(a[0]));
                pop_al.add(Integer.parseInt(a[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] age = new int[age_al.size()];
        for (int i = 0; i < age.length; i++) {
            age[i] = age_al.get(i).intValue(); // ArrayList를 Array로 변환
        }

        int[] pop = new int[pop_al.size()];
        for (int i = 0; i < pop.length; i++) {
            pop[i] = pop_al.get(i).intValue(); // ArrayList를 Array로 변환
        }

        Random random = new Random();
        int[] age_selected = new int[10];
        for (int i = 0; i < 10; i++) {
            age_selected[i] = random.nextInt(40); // 임의로 후보해 10개 선택
        }

        int hubohae[][] = new int[10][2];
        for (int w = 0; w < 10; w++) {
            for (int i = 0; i < 40; i++) {
                if (age_selected[w] == age[i]) {
                    hubohae[w][0] = age[i];
                    hubohae[w][1] = pop[i]; // 선택된 후보해(나이)의 인구 배열 생성
                    //System.out.printf("%d %d \n", hubohae[w][0], hubohae[w][1]);
                }
            }
        }

        int[] gradient_frequency = new int[100];
        int check = 0;
        for (int i = 0; i < 100; i++) {
            check++;
            int[] jhd = jukhapdo(hubohae); // 적합도 계산
            int[] sx = selection(jhd); // 선택 연산
            String[] cx = crossOver(sx); // 교차 연산
            int[] mx = mutation(cx); // 돌연변이 연산
            gradient_frequency[i] = count(mx); // 가장 많이 나온 기울기 선택
            for(int k = 0; k<check;k++) {
                // System.out.printf("%d ", gradient_frequency[k]);
            } // System.out.println();
        }

        int[] grad = new int [check];
        for(int i = 0; i < check; i++) {
            grad[i] = gradient_frequency[i];
        }

        int gradient = count(grad); // 반복하여 계산한 기울기 중에서 가장 많이 나온 기울기 선택
        for(int i=0;i<check;i++) {
            // System.out.printf("%d ", gradient_frequency[i]);
        } // System.out.println();

        System.out.printf("구하고자 하는 함수는 " + "y = " + gradient + "x + 117 이다.");
    }
}