public class Grid {
    public static void main(String[] args) {
        if (args.length == 0){
            System.out.println("The command line needs two integers");

        }
        int yogi = 0;
        int booboo = 10;
        yogi = Integer.parseInt(args[0]);
        booboo = Integer.parseInt(args[1]);
        String[][] largBoi = new String[yogi][booboo];
        for (int i = 0; i < largBoi.length ; i++) {
            for (int j = 0; j < largBoi[0].length ; j++) {
                largBoi[i][j] = (char) (int)(Math.random()* 93+ 33) + "";

            }

        }
        for (int i = 0; i < largBoi.length ; i++) {
            String row = "";
            for (int j = 0; j < largBoi[0].length; j++) {
                row += largBoi[i][j] + "\t";
            }
            System.out.println(row);
        }

    }
}
