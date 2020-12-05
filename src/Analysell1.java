/**
 * @author Lihu
 * @PROJECT_NAME: java-LL1
 * @DESCRIPTION:
 * @USER: Irene-Jisoo
 * @DATE: 2020/11/22 19:50
 */

import java.util.*;


public class Analysell1 {
    static Map<Character, ArrayList<String>> map = new HashMap<>();//储存产生式，把右端的字符串放入list集合中
    static HashMap<Character, HashSet<Character>> FirstSet = new HashMap<>();//储存FIRST集合
    static HashMap<String, HashSet<Character>> FirstSetX = new HashMap<>();//储存任意符号串的first集
    static ArrayList<Character> no_end = new ArrayList<Character>();//储存非终结符ERTWF
    static ArrayList<Character> end = new ArrayList<Character>();//储存终结符+*()i
    static HashMap<Character, HashSet<Character>> FollowSet = new HashMap<>();//储存Follow集
    static char start = 'E';
    static String inStr = new String();//储存输入字符串i*i+i*i#
    static String[][] table;//预测分析表
    static Stack<Character> stack = new Stack<>();  //符号栈
    static int index = 0;//输入字符指针
    static String action = "";
    static int step = 0;//记录分析过程的步骤数

    static void First() {
        //遍历求每一个非终结符vn的first集
        for (Character vn : no_end
        ) {
            getfisrst(vn);
        }
    }

    /**
     * 生成非终结符FIRST集的递归程序
     */
    static void getfisrst(Character ch) {     //E
        ArrayList<String> ch_production = map.get(ch);
        HashSet<Character> set = FirstSet.containsKey(ch) ? FirstSet.get(ch) : new HashSet<>();//判断该非终结符是否已经存入到first集中，如果有得出他之间求的first集中，然后把求得新的加入其中
        // 当ch为终结符
        if (end.contains(ch)) {
            set.add(ch);
            FirstSet.put(ch, set);//终结符加入对应的非终结符的first集中
            return;
        }
        //ch为非终结符
        for (String str : ch_production//TR
        ) {
            int i = 0;
            while (i < str.length()) {
                char tn = str.charAt(i);//T
                //递归
                getfisrst(tn);//递归求出T的first集
                HashSet<Character> tvSet = FirstSet.get(tn);//得到T的first集
                // 将其first集加入左部
                for (Character tmp : tvSet) {
                    if (tmp != 'ε')
                        set.add(tmp);//把T的first集加入E的
                }
                // 若包含空串 处理下一个符号
                if (tvSet.contains('ε'))
                    i++;//如果运行到这说明T->ε,接着求R
                    // 否则退出 处理下一个产生式
                else
                    break;
            }
            if (i == str.length())
                set.add('ε');//产生式并没有求到First集时加一个空的字符进入
        }
        FirstSet.put(ch, set);//把所存的E的FIRST集set加入E的关键字所对应的hashmap中。
    }

    /**
     * 生成任何符号串的first
     */
    static void getFirstX(String s) {

        HashSet<Character> set = (FirstSetX.containsKey(s)) ? FirstSetX.get(s) : new HashSet<Character>();
        // 从左往右扫描该式
        int i = 0;
        while (i < s.length()) {
            char tn = s.charAt(i);
            if (!FirstSet.containsKey(tn))
                getfisrst(tn);
            HashSet<Character> tvSet = FirstSet.get(tn);
            // 将其非空 first集加入左部
            for (Character tmp : tvSet)
                if (tmp != 'ε')
                    set.add(tmp);
            // 若包含空串 处理下一个符号
            if (tvSet.contains('ε'))
                i++;
                // 否则结束
            else
                break;
            // 到了尾部 即所有符号的first集都包含空串 把空串加入
            if (i == s.length()) {
                set.add('ε');
            }
        }
        FirstSetX.put(s, set);


    }

    static void Follow() {
        //此处我多循环了几次，合理的方法应该是看每一个非终结符的follow集师傅增加，不增加即可停止循环。
        for (int i = 0; i < 3; i++) {
            for (Character ch : no_end
            ) {
                getFollow(ch);
            }
        }

    }

    static void getFollow(char c) {
        ArrayList<String> list = map.get(c);//获得产生式
        HashSet<Character> setA = FollowSet.containsKey(c) ? FollowSet.get(c) : new HashSet<Character>();
        //如果是开始符 添加 #
        if (c == start) {
            setA.add('#');
        }
        //查找输入的所有产生式，确定c的后跟 终结符
        for (Character ch : no_end) {
            ArrayList<String> l = map.get(ch);
            for (String s : l)
                for (int i = 0; i < s.length(); i++)
                    if (s.charAt(i) == c && i + 1 < s.length() && end.contains(s.charAt(i + 1)))
                        setA.add(s.charAt(i + 1));
        }
        FollowSet.put(c, setA);
        //处理c的每一条产生式
        for (String s : list) {
            int i = s.length() - 1;
            while (i >= 0) {
                char tn = s.charAt(i);
                //只处理非终结符
                if (no_end.contains(tn)) {

                    if (s.length() - i - 1 > 0) {
                        String right = s.substring(i + 1);
                        //非空first集 加入 followB
                        HashSet<Character> setF = null;
                        if (right.length() == 1) {
                            if (!FirstSet.containsKey(right.charAt(0)))
                                getfisrst(right.charAt(0));
                            setF = FirstSet.get(right.charAt(0));
                        } else {
                            //先找出右部的first集
                            if (!FirstSetX.containsKey(right))
                                getFirstX(right);
                            setF = FirstSetX.get(right);
                        }
                        HashSet<Character> setX = FollowSet.containsKey(tn) ? FollowSet.get(tn) : new HashSet<Character>();
                        for (Character var : setF)
                            if (var != 'ε')
                                setX.add(var);
                        FollowSet.put(tn, setX);

                        // 若first(β)包含空串   followA 加入 followB
                        if (setF.contains('ε')) {
                            if (tn != c) {
                                HashSet<Character> setB = FollowSet.containsKey(tn) ? FollowSet.get(tn) : new HashSet<Character>();
                                for (Character var : setA)
                                    setB.add(var);
                                FollowSet.put(tn, setB);
                            }
                        }
                    }
                    //若β不存在   followA 加入 followB
                    else {
                        // A和B相同不添加
                        if (tn != c) {
                            HashSet<Character> setB = FollowSet.containsKey(tn) ? FollowSet.get(tn) : new HashSet<Character>();
                            for (Character var : setA)
                                setB.add(var);
                            FollowSet.put(tn, setB);
                        }
                    }
                    i--;
                }
                else i--;
            }
        }
    }

    /**
     * 生成预测分析表
     */
    static void creatTable() {
        Object[] VtArray = end.toArray();
        Object[] VnArray = no_end.toArray();
        // 预测分析表初始化
        table = new String[VnArray.length + 1][VtArray.length + 1];

        table[0][0] = "Vn/Vt";
        //初始化首行首列
        for (int i = 0; i < VtArray.length; i++) {
            // table[i]=new String[VtArray.length + 1];
            table[0][i + 1] = (VtArray[i].toString().charAt(0) == 'ε') ? "#" : VtArray[i].toString();
        }

        for (int i = 0; i < VnArray.length; i++)
            table[i + 1][0] = VnArray[i] + "";
        //全部置error
        for (int i = 0; i < VnArray.length; i++)
            for (int j = 0; j < VtArray.length; j++)
                table[i + 1][j + 1] = "error";
        //插入生成式
        for (char A : no_end) {
            ArrayList<String> l = map.get(A);
            for (String s : l) {
                HashSet<Character> set = FirstSetX.get(s);
                for (char a : set)
                    insert(A, a, s);
                if (set.contains('ε')) {
                    HashSet<Character> setFollow = FollowSet.get(A);
                    if (setFollow.contains('#'))
                        insert(A, '#', s);
                    for (char b : setFollow)
                        insert(A, b, s);
                }
            }
        }
    }

    static void insert(char X, char a, String s) {
        if (a == 'ε') a = '#';
        for (int i = 0; i < no_end.size() + 1; i++) {
            if (table[i][0].charAt(0) == X)
                for (int j = 0; j < end.size() + 1; j++) {
                    if (table[0][j].charAt(0) == a) {
                        table[i][j] = s;
                        return;
                    }
                }
        }
    }

    //进行语法分析
    static void processLL1() {
        System.out.println(inStr + "的分析过程");
        System.out.println("步骤             分析符号栈          剩余输出串     推导产生式或匹配");
        stack.push('#');
        stack.push('E');//将#E进行入栈
        displayLL();
        char X = stack.peek();//X等于栈顶元素E
        while (X != '#') {
            char a = inStr.charAt(index);//输入串首字母i
            if (X == a) {
                action = stack.peek() + "匹配 ";
                stack.pop();//两者匹配时移除E
                index++;//确保下次啊指向字符串下一个字符 +

            } else if (find(X, a).equals("error")) {
                boolean flag = false;
                if (FirstSet.get(X).contains('ε')) {
                    action = X + "->ε";
                    stack.pop();
                    flag = true;
                }
                if (!flag) {
                    action = "error";
                    displayLL();
                    return;
                }

            } else if (find(X, a).equals("ε")) {
                stack.pop();
                action = X + "->ε";

            } else {
                String str = find(X, a);
                if (str != "") {
                    action = X + "->" + str;
                    stack.pop();
                    int len = str.length();
                    String pushStr = "";
                    for (int i = len - 1; i >= 0; i--) {
                        stack.push(str.charAt(i));
                        pushStr += str.charAt(i);
                    }

                } else {
                    System.out.println("error at '" + inStr.charAt(index) + " in " + index);
                    return;
                }
            }
            X = stack.peek();
            displayLL();
        }
        System.out.println(inStr + "是给定文法的句子");

    }


    //  预测分析表中对应内容

    static String find(char X, char a) {
        for (int i = 0; i < no_end.size() + 1; i++) {
            if (table[i][0].charAt(0) == X)
                for (int j = 0; j < end.size() + 1; j++) {
                    if (table[0][j].charAt(0) == a)
                        return table[i][j];
                }
        }
        return "";
    }

    static void displayLL() {
        // 输出 LL1单步处理
        Stack<Character> s = stack;
        System.out.print(step++);//记录一共有多少步骤
        System.out.printf("%23s", s);
        System.out.printf("%21s", inStr.substring(index));
        System.out.printf("%20s", action);
        System.out.println();
    }

    public static void main(String[] args) {


        no_end = input_.input("请输入非终结符：");
        end = input_.input("请输入终结符：");
        System.out.print("请输入产生式的个数：");
        int num = 0;
        Scanner in = new Scanner(System.in);
        if (in.hasNext()) num = in.nextInt();
//        char [][]production =new char[num][];
        String production[] = new String[num];//{"E->TR","R->+T","T->FW","W->*F","W->*F","F->(E)","F->i"};
        System.out.println("请输入文法" + num + "个产生式，并以回车分隔每个产生式");
        //System.out.println(production.length);
        for (int i = 0; i < production.length; i++) {
            String arr = null;
            System.out.format("请输入第%d个：", i);
            Scanner put = new Scanner(System.in);
            if (put.hasNext()) {
                arr = put.nextLine();
            }
            production[i] = arr;
        }
//        for (int j = 0; j < production.length; j++) {
//            System.out.println(production[j]);
//        }

        for (String str : production) {
            String strs[] = str.split("->");//将产生式的左部和右部分开
            Character vch = strs[0].charAt(0);
            ArrayList<String> list = new ArrayList<String>();
            if (map.containsKey(vch)) list = map.get(vch);
            list.add(strs[1]);
            map.put(vch, list);//将产生式存入

        }
        First();
        System.out.println("所得FIRST集合为：");
        for (Character c :
                no_end) {
            System.out.print(c + ":      ");
            System.out.println(FirstSet.get(c));
        }
        //System.out.println(FirstSet);
        for (Character c : no_end) {
            ArrayList<String> l = map.get(c);
            for (String s : l)
                getFirstX(s);
        }
        Follow();
        // System.out.println(FollowSet);
        System.out.println("所得FOLLOW集合为：");
        for (Character c :
                no_end) {
            System.out.print(c + ":      ");
            System.out.println(FollowSet.get(c));
        }
        //    System.out.println(FirstSetX);

        creatTable();
        //System.out.println(table[0][0]);
        System.out.println("构造分析表如下：");
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                System.out.printf("%-10s", table[i][j] + "  ");

            }
            System.out.println();

        }
        System.out.print("是否继续进行句型分析？<y/n>:");
        String flag = in.next();
        if (flag.equals("y")) {
            System.out.print("请输入符号串<以#结束>：");
            if (in.hasNext()) {
                inStr = in.next();
            }
//            System.out.println(inStr);
            processLL1();
        }

    }
}
/**使用  Map<String, String[]>map来储存产生式**/
    /* int flag[]=new int[5];

        ArrayList<String> sp= new ArrayList<String>();
        Map<String, String[]>map=new HashMap<>();
        for (int i = 0; i <production.length; i++) {
            int nu=0;
            String split1[] = production[i].split("->");
            String spilt2[]=new String[production.length];
            spilt2[nu]=split1[1];

            for (int j = 0; j <production.length ; j++) {
                String spl[] = production[j].split("->");
                if(j!=i&&spl[0].equals(split1[0])){
                    nu++;
                    spilt2[nu]=spl[1];
                }



            }
            map.put(split1[0],spilt2);
        }

        Iterator<String> it =map.keySet().iterator();
        while(it.hasNext()) {
            String str=(String )it.next();
            String name[]=map.get(str);
            System.out.println(str+"    __   "+Arrays.toString(name));
        }

        new First(map).firstKernealCode();*/
