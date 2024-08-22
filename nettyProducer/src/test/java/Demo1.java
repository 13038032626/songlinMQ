import java.util.Arrays;

public class Demo1 {
    public static int[] quickSort(int[] ints,int l,int r){

        if(r == l){
            return ints;
        }
        int mid = (l + r)/2;
        int[] help = new int[ints.length];
        int smaller = l;
        int bigger = r;
        int pivot = ints[mid];
        int currMid = 0;
        for (int i = l; i <= r; i++) {
            int curr = ints[i];
            if(curr < pivot){
                help[smaller] = curr;
                smaller++;
            }else if(curr > pivot){
                help[bigger] = curr;
                bigger--;
            }else {
                currMid = smaller;
            }
        }
        for (int i = l; i <= r; i++) {
            ints[i] = help[i];
        }

        quickSort(ints,l,currMid);
        quickSort(ints,currMid+1,r);

        return ints;
    }


    public static void main(String[] args) {
        System.out.println(Arrays.toString(Demo1.quickSort(new int[]{1, 4, 2, 8, 7, 5}, 0, 5)));
    }
}
