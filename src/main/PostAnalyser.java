import java.util.*;

class PostAnalyser{

    static void getPost(){

        String url = "http://tieba.baidu.com/p/5254848189";
        String post = PageRequester.getHTML(url);

        //ArrayList<String[]> result = Tool.analysisPost(post);//共26项
        ArrayList<String[]> result = Tool.analysisComment(post);//共有7项
        //显示帖子结果
        System.out.println("result.size()" + result.size() + "n" + "result.get(0).length  " + result.get(0).length);
        for(int i = 0; i < result.size(); i++){
            for(int j = 0; j < result.get(0).length; j++){
                System.out.println("**floors  " + j + "    " + result.get(i)[j]);
            }
            System.out.print(i + 1 + "\n");
        }
    }


}
