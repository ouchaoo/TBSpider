import java.util.ArrayList;

class ThreadIDFactory{



    /**
     * 启动器，被主线程调传入要爬取的贴吧名称用而启动
     * 将帖子的相关信息传入到数据库
     *
     * @param  String homePage      贴吧名称
     */
    static void produceID(String tiebaName){
        String homePage = PageRequester.getHTML("http://tieba.baidu.com/f?kw=" + tiebaName + "ie=utf-8");

        ArrayList<String[]> metaMessage = Tool.analysisThread(homePage);

        ArrayList<String> IDlist = new ArrayList<>();

        /*//构建帖子ID List作为返回结果
        for(int i = 0; i < metaMessage.size(); i++){
            IDlist.add(metaMessage.get(i)[0]);
            //结果测试
            for(int j = 0; j < metaMessage.get(0).length; j++){
                System.out.println( "index:" + i + "   " + j + "  " + metaMessage.get(i)[j]);
            }
            System.out.println("");
        }*/

        storeThread(metaMessage, "湖南中医药大学");

    }

    /**
     * TODO 对外接口，用于被PostAnalyser调用，返回一组ThreadID 及其贴吧名
     * @return  一组ThreadID 及其贴吧名
     */
    static ArrayList<String[]> getThreadId(){}

    /**
     * 将主题帖（thread）信息传入数据库
     * @param ArrayList<String[]> thread    主题帖（thread）信息
     * @param String              tiebaName 贴吧名
     */
    static void storeThread(ArrayList<String[]> thread, String tiebaName){
        ArrayList<String[]> formatedData = new ArrayList<>();
        String[] tmp = new String[15];
        for(int i = 0; i < thread.size(); i++){
            tmp = new String[15];

            tmp[0] = tiebaName;//tieba_name
            tmp[1] = thread.get(i)[0];//thread_id
            tmp[2] = thread.get(i)[12];//title
            tmp[3] = thread.get(i)[2];//first_post_id
            tmp[4] = thread.get(i)[13];//user_id
            tmp[5] = "null";//data
            tmp[6] = Tool.getNowTime();//update_time
            tmp[7] = thread.get(i)[4];//is_bakan
            tmp[8] = thread.get(i)[5];//vid
            tmp[9] = thread.get(i)[6];//is_good
            tmp[10] = thread.get(i)[7];//is_top
            tmp[11] = thread.get(i)[8];//is_protal
            tmp[12] = thread.get(i)[9];//is_membertop
            tmp[13] = thread.get(i)[10];//is_multi_forum
            tmp[14] = thread.get(i)[11];//frs_tpiont

            formatedData.add(tmp);
        }

        Database.insertThread(formatedData);
    }
}



















//
