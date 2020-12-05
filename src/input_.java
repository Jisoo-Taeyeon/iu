/**
 * @author Lihu
 * @PROJECT_NAME: java-LL1
 * @DESCRIPTION:
 * @USER: Irene-Jisoo
 * @DATE: 2020/11/22 20:01
 */

 /**
 * 用于实现终结符和非终结符的输入并存入list表中
 * */
import java.util.ArrayList;
import java.util.Scanner;

class input_ {
    public  static ArrayList input(String s){
        ArrayList<Character> end=new ArrayList<Character>();
        Scanner in= new Scanner(System.in);
        String end_ = null;
        while (true){
            System.out.println(s);
            if(in.hasNext())
                end_=in.nextLine();
            String flag=null;
            System.out.print("请输入正确确认？<y/n>:");
            if(in.hasNext())
                flag=in.nextLine();
            if(flag.equals("y"))
                break;
        }

        char[] ends=end_.toCharArray();//将String类转换为char型数组；
        for(int i=0;i<end_.length();i++){
            if(ends[i]!='#')
                end.add(ends[i]);//String.valueOf可以将char类转换为字符类
        }
        System.out.println(end);
        return end;
    }
}
