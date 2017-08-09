
public class Start {

    public static void main(String[] args) throws Exception {

        //PostAnalyser.getPost();
        new ThreadIDFactory("湖南中医药大学").produceID();

        /*String url = "http://tieba.baidu.com/p/5219298388";
        String post = PageRequester.getHTML(url);
        System.out.println("***********");
        System.out.println(post);
        int num = Tool.getPostPageNum(post);*/


    }

}

//TODO 解决正则表达式报错, 为PageRequester 增加自动等待功能，防止被禁止
/*
正在爬取帖子 5250129012 的第 1 页
开始分析回帖
开始分析评论
正在爬取帖子 5250129012 的第 2 页
开始分析回帖
开始分析评论
正在爬取帖子 5184686766 的第 1 页
开始分析回帖
Exception in thread "Thread-0" java.lang.IndexOutOfBoundsException: Index: 11, Size: 11
	at java.util.ArrayList.rangeCheck(ArrayList.java:653)
	at java.util.ArrayList.get(ArrayList.java:429)
	at Tool.analysis(Tool.java:151)
	at Tool.analysisPost(Tool.java:30)
	at PostAnalyser.run(PostAnalyser.java:49)
	at java.lang.Thread.run(Thread.java:748)

 */
