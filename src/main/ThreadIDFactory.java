import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.Runnable;
import java.util.*;

class ThreadIDFactory{

    Deque<Integer> taskList;
    ConcurrentLinkedQueue<String> IDlist;
    String tiebaName;

    ThreadIDFactory(String tiebaName){
        taskList = new ArrayDeque<Integer>();
        for(int i = 0; i < 5; i++){
            taskList.add(i);
        }
        IDlist = new ConcurrentLinkedQueue<String>();
        this.tiebaName = tiebaName;
    }

    /**
     * 爬取贴吧主页获得 threadID ,
     * 将帖子的相关信息传入到数据库。
     * 任务管理中枢，通过磁盘文件记录爬取的进度，实现“断点续爬”
     *
     * @param  String homePage      贴吧名称
     */
    void produceID(){

        new Thread(new PostAnalyser(this)).start();//复制此语句可增加爬虫线程数

        String[] log = Tool.getThreadLog();
        System.out.println("载入threadLog成功.....");
        int allPageNum = Integer.parseInt(log[1]);

        int nowPage = taskList.peek();//当前页码减一
        boolean latestThreadFlag = true;//标记latestThread是否还可以修改
        int target = 1000;//目标页数

        while(nowPage < (target - 1) ){

            nowPage = taskList.poll();
            String url = "http://tieba.baidu.com/f?kw=" + this.tiebaName + "&ie=utf-8&pn=" +
                nowPage*50;
            String homePage = PageRequester.getHTML(url);
            System.out.println("当前tiebaName: " + this.tiebaName);
            System.out.println("当前url: " + url);


            ArrayList<String[]> metaMessage = Tool.analysisThread(homePage);
            System.out.println("抓取到新thread页" + metaMessage.size() );
            for(int j = 0; j < metaMessage.size(); j++){
                //数据成功存入数据库的帖子
                if(storeThread(metaMessage.get(j), this.tiebaName)){
                //if(true){
                    IDlist.add(metaMessage.get(j)[0]);
                    //处在第一页的新帖，latestThread出于可修改状态，且该帖不是置顶帖
                    if(nowPage == 0 && latestThreadFlag && !metaMessage.get(j)[7].equals("true")){
                        Tool.setThreadLog(metaMessage.get(j)[0]);
                        System.out.println("修改latestThread为" + metaMessage.get(j)[0]);
                        latestThreadFlag = false;
                    }
                }
                //回到了上次的最新位置,修改任务列表，跳过已经爬过的内容
                else if(metaMessage.get(j)[0].equals(log[0]) && !metaMessage.get(j)[7].equals("true")){
                    taskList.clear();
                    taskList.add(++nowPage);
                    int n = nowPage + Integer.parseInt(log[1]) - 2;
                    n = n < 0 ? 0 : n;//排除n小于0 的情况
                    for(int m = 0; m < 5; m++){
                        taskList.add(n++);
                    }
                    System.out.println("回到了上次的位置 " );
                }

            }
            System.out.println("正在爬取主页第 " + (nowPage + 1) + " 页的帖子.......");

            //修改已经爬过的页数
            if(nowPage > 4){
                Tool.setThreadLog(nowPage + 1);
            }
            //填充taskLi
            if(taskList.size() < 3){
                int num = taskList.size();
                taskList.add(taskList.peek() + 1);
                taskList.add(taskList.peek() + 1);
            }

            //当剩余的threadID 足够时，本线程暂时进入睡眠状态
            while(true){
                if(IDlist.size() > 50){
                    try{
                        Thread.sleep(30000);
                    }
                    catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
                else{
                    break;
                }
            }

        }

        IDlist.add("stop");
    }

    /**
     * 对外接口，用于被PostAnalyser调用，返回一组ThreadID 及其贴吧名
     * @return  ThreadID 及其贴吧名
     */
    String[] getThreadId(){
        String[] result = new String[2];
        while(true){
            result[1] = this.tiebaName;
            if(IDlist.size() != 0){
                result[0] = IDlist.poll();
                break;
            }
            else{
                try{
                    System.out.println("IDlist 已经空啦！ 此线程暂时睡眠......");
                    Thread.sleep(10000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }

            }
        }


        return result;
    }

    /**
     * 将主题帖（thread）信息传入数据库
     * @param ArrayList<String[]> thread    主题帖（thread）信息
     * @param String              tiebaName 贴吧名
     */
    static boolean storeThread(String[] thread, String tiebaName){
        String[] formatedData = new String[15];

        formatedData[0] = tiebaName;//tieba_name
        formatedData[1] = thread[0];//thread_id
        formatedData[2] = thread[12];//title
        formatedData[3] = thread[2];//first_post_id
        formatedData[4] = thread[13];//user_id
        formatedData[5] = "1900-01-01 00:00:00";//data
        formatedData[6] = Tool.getNowTime();//update_time
        formatedData[7] = thread[4];//is_bakan
        formatedData[8] = thread[5];//vid
        formatedData[9] = thread[6];//is_good
        formatedData[10] = thread[7];//is_top
        formatedData[11] = thread[8];//is_protal
        formatedData[12] = thread[9];//is_membertop
        formatedData[13] = thread[10];//is_multi_forum
        formatedData[14] = thread[11];//frs_tpiont

        return Database.insertThread(formatedData);
    }
}
