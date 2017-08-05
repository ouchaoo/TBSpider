import java.util.ArrayList;

class ThreadIDFactory{



    /**
     * 通过贴吧名称，获得贴吧帖子ID的列表。
     * TODO 将帖子的相关信息存入到数据库
     *
     * @param  String homePage      贴吧名称
     * @return        帖子ID列表
     */
    static void produceID(String tiebaName){
        String homePage = PageRequester.getHTML("http://tieba.baidu.com/f?kw=" + tiebaName + "ie=utf-8");

        ArrayList<String[]> metaMessage = Tool.analysisThread(homePage);

        ArrayList<String> IDlist = new ArrayList<>();

        //构建帖子ID List作为返回结果
        for(int i = 0; i < metaMessage.size(); i++){
            IDlist.add(metaMessage.get(i)[0]);
            /*//结果测试
            System.out.print("\nindex:  " + i + "   ");
            for(int j = 0; j < metaMessage.get(0).length; j++){
                System.out.print( j + "  " + metaMessage.get(i)[j] + "    ");
            }*/
        }

    }
}
