import java.util.*;

class PostAnalyser implements Runnable{
    ThreadIDFactory thread;

    /**
     * 帖子爬取程序的构造器，传入ThreadIDFactory 对象，用于向其请求 threadID
     */
    PostAnalyser(ThreadIDFactory thread){
        this.thread = thread;
    }

    /**
     * 帖子爬取的主题程序，可多线程运行
     */
    public void run(){
        try{
            Thread.sleep(10000);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("帖子内容爬取线程启动");
        while(true){
            String[] threadID = thread.getThreadId();
            if(threadID[0].equals("stop")){
                break;
            }

            String url = "http://tieba.baidu.com/p/" + threadID[0];
            String post = PageRequester.getHTML(url);
            int pageNum = Tool.getPostPageNum(post);
            if(pageNum == 0){
                /*url = "https://tieba.baidu.com/p/" + threadID[0];//改为https 连接
                System.out.println("修改后URL为   " + url);
                post = PageRequester.getHTML(url);
                pageNum = Tool.getPostPageNum(post);*/
                System.out.println("此贴结构暂未匹配，跳过");
                continue;
            }
            else if( pageNum < 0){
                System.out.println("未知错误，跳过thread " + threadID[0]);
                continue;
            }

            System.out.println("正在爬取帖子 " + threadID[0] + " ,共有 " + pageNum + " 页");
            for(int i = 0; i < pageNum; i++){
                if(i == 0){
                    //帖子第一页，重复利用之前的请求到的结果,不必再重新请求
                }
                else{
                    url = "http://tieba.baidu.com/p/" + threadID[0] + "?pn=" + i;
                    post = PageRequester.getHTML(url);
                }
                ArrayList<String[]> postItem = Tool.analysisPost(post);//共26项
                storePost(postItem, threadID[0], threadID[1]);
                ArrayList<String[]> commentItem = Tool.analysisComment(post);//共有7项
                storeComment(commentItem, threadID[1]);
                UserAnalyser.sendTo(postItem);
            }

        }

        System.out.println("本次爬虫任务结束");
    }


    /**
     * 将通过正则表达式提取到的回帖信息格式化，并传入数据库
     * @param ArrayList<String[]> postItem   正则表达式提取到的回帖信息
     * @param String              tieba_name 贴吧名
     * @param String              thread_id  主题帖(thread)ID
     */
    static void storePost(ArrayList<String[]> postItem, String thread_id, String tieba_name){
        ArrayList<String[]> formatedData = new ArrayList<>();
        String[] tmp = new String[17];
        for(int i = 0; i < postItem.size(); i++){
            tmp = new String[17];
            tmp[0] = tieba_name;//tieba_name
            tmp[1] = thread_id;//thread_id
            tmp[2] = postItem.get(i)[11];//post_id
            tmp[3] = postItem.get(i)[0];//user_id
            tmp[4] = postItem.get(i)[25];//content
            tmp[5] = postItem.get(i)[17];//post_no
            tmp[6] = postItem.get(i)[23];//post_index
            tmp[7] = postItem.get(i)[15];//data
            tmp[8] = postItem.get(i)[12];//is_anonym
            tmp[9] = postItem.get(i)[13];//open_id
            tmp[10] = postItem.get(i)[14];//open_type
            tmp[11] = postItem.get(i)[16];//vote_crypt
            tmp[12] = postItem.get(i)[18];//type
            tmp[13] = postItem.get(i)[20];//ptype
            tmp[14] = postItem.get(i)[21];//is_saveface
            tmp[15] = postItem.get(i)[22];//props
            tmp[16] = postItem.get(i)[24];//pb_tpoint

            formatedData.add(tmp);
        }

        Database.insertPost(formatedData);
    }

    /**
     * 将通过正则表达式提取到的评论信息格式化，并传入数据库
     * @param ArrayList<String[]> commentItem 正则表达式提取到的评论信息
     * @param String              tieba_name  贴吧名
     */
    static void storeComment(ArrayList<String[]> commentItem, String tieba_name){
        ArrayList<String[]> formatedData = new ArrayList<>();
        String[] tmp = new String[9];
        for(int i = 0; i < commentItem.size(); i++){
            tmp = new String[9];
            tmp[0] = tieba_name;//tieba_name
            tmp[1] = commentItem.get(i)[0];//post_id
            tmp[2] = commentItem.get(i)[1];//comment_id
            tmp[3] = commentItem.get(i)[2];//user_id
            tmp[4] = Tool.stampToDate(commentItem.get(i)[3]);//now_time
            tmp[5] = Tool.unicode2Str(commentItem.get(i)[4]);//content
            tmp[6] = commentItem.get(i)[5];//ptype
            tmp[7] = commentItem.get(i)[6];//during_time
            //tmp[8] = direct_user_id  评论回复的用户ID

            formatedData.add(tmp);
        }

        Database.insertComment(formatedData);
    }


}
