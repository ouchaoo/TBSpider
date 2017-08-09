import java.util.*;

class PostAnalyser implements Runnable{
    ThreadIDFactory thread;

    PostAnalyser(ThreadIDFactory thread){
        this.thread = thread;
    }

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
                url = "https://tieba.baidu.com/p/" + threadID[0];//改为https 连接
                System.out.println("修改后URL为   " + url);
                post = PageRequester.getHTML(url);
                pageNum = Tool.getPostPageNum(post);
            }
            else if( pageNum < 0){
                System.out.println("未知错误，跳过thread " + threadID[0]);
                continue;
            }

            for(int i = 0; i < pageNum; i++){
                System.out.println("正在爬取帖子 " + threadID[0] + " 的第 " + (i + 1) + " 页");
                if(i == 0){
                    //String post = firstPage;
                }
                else{
                    url = "http://tieba.baidu.com/p/" + threadID[0] + "?pn=" + i;
                    post = PageRequester.getHTML(url);
                }
                System.out.println("开始分析回帖");
                ArrayList<String[]> postItem = Tool.analysisPost(post);//共26项
                storePost(postItem, threadID[0], threadID[1]);
                System.out.println("开始分析评论");
                ArrayList<String[]> commentItem = Tool.analysisComment(post);//共有7项
                storeComment(commentItem, threadID[1]);
                UserAnalyser.sendTo(postItem);
            }

        }

        System.out.println("本次爬虫任务结束");
    }

    /**
     * 启动器，用于被主进程调用，启动对帖子的爬取，并将爬取的结果传入数据库
     */
    void getPost(){






        /*//显示帖子结果
        System.out.println("result.size()" + result.size() + "n" + "result.get(0).length  " + result.get(0).length);
        for(int i = 0; i < result.size(); i++){
            for(int j = 0; j < result.get(0).length; j++){
                System.out.println("**floors  " + j + "    " + result.get(i)[j]);
            }
            System.out.print(i + 1 + "\n");
        }*/


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
