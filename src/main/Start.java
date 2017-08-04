import java.net.HttpURLConnection;
import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.util.*;


public class Start {

    public static void main(String[] args) throws Exception {

        //String urlStr = "http://tieba.baidu.com/p/5237958977";
        //String html = getHTML(urlStr);
        /* //用于将网页文件保存到本地
        String html = "";
        FileReader fr = new FileReader("post.html");
        char[] cbuf = new char[32];
        int hasRead = 0;
        while((hasRead = fr.read(cbuf)) > 0){
            html += new String(cbuf, 0, hasRead);
        }*/

        //提取帖子信息的正则表达式
        /*String messageRe = new String(
        "\\{&quot;author&quot;:\\{&quot;user_id&quot;:(\\d*),&quot;user_name&quot;:&quot;(.*)&quot;,&quot;name_u&quot;:&quot;(.*)&quot;,&quot;user_sex&quot;:(\\d),&quot;portrait&quot;:&quot;(.*)&quot;,&quot;is_like&quot;:(\\d),&quot;level_id&quot;:(\\d*),&quot;level_name&quot;:&quot;(.*)&quot;,&quot;cur_score&quot;:(\\d*),&quot;bawu&quot;:(\\d*),&quot;props&quot;:(.*)\\},&quot;content&quot;:\\{&quot;post_id&quot;:(\\d*),&quot;is_anonym&quot;:(.*),&quot;open_id&quot;:&quot;(.*)&quot;,&quot;open_type&quot;:&quot;(.*)&quot;,&quot;date&quot;:&quot;(.*)&quot;,&quot;vote_crypt&quot;:&quot;(.*)&quot;,&quot;post_no&quot;:(\\d*),&quot;type&quot;:&quot;(\\d*)&quot;,&quot;comment_num&quot;:(\\d*),&quot;ptype&quot;:&quot;(\\d*)&quot;,&quot;is_saveface&quot;:(.*),&quot;props&quot;:(.*),&quot;post_index&quot;:(\\d*),&quot;pb_tpoint&quot;:(.*)\\}\\}?"
        );
        Pattern messagePa = Pattern.compile(messageRe);
        //提取帖子内容的正则表达式
        String contentRe = new String("j_d_post_content  clearfix\">(.*)</div><br></cc>?");
        Pattern contentPa = Pattern.compile(contentRe);

        ArrayList<String[]> result = analysisPost(html, messagePa, contentPa);

        System.out.println("result.size()" + result.size() + "\n" + "result.get(0).length  " + result.get(0).length);*/

        /*for(int i = 0; i < result.size(); i++){
            for(int j = 0; j < result.get(0).length; j++){
                System.out.println("**floors  " + i + "    " + result.get(i)[j]);
            }
            System.out.print(i + 1 + "\n");
        }*/
        ArrayList<String> postID = getPostID("湖南中医药大学");
        for(int i = 0; i < postID.size(); i++){
            System.out.println(postID.get(i));
        }

    }

    /**
     * 通过帖子URL获得帖子文本
     * @param  String urlStr        帖子URL
     * @return        String格式的帖子HTML源码
     */
    static String getHTML(String urlStr){
        System.out.println("**request html .....");
        URL url = null;
        String result = "";
        HttpURLConnection conn = null;
        BufferedReader in = null;
        try{
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty(
                    "Accept",
                    "image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
                            + "application/x-shockwave-flash, application/xaml+xml, "
                            + "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
                            + "application/x-ms-application, application/vnd.ms-excel, "
                            + "application/vnd.ms-powerpoint, application/msword, */*");
            conn.setRequestProperty("Accept-Language", "zh-CN");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");

            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line = "";
            while((line = in.readLine()) != null) {
                result += line + "\n";
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                in.close();
            }
            catch(Exception e2){
                e2.printStackTrace();
            }
        }
        System.out.println("**get html!");
        return result;
    }




    /**
     * 通过贴吧名称，获得贴吧帖子ID的列表。XXX 将帖子的相关信息存入到数据库
     * @param  String homePage      贴吧名称
     * @return        帖子ID列表
     */
    static ArrayList<String> getPostID(String homePage){
        String homePageURL = getHTML("http://tieba.baidu.com/f?kw=" + homePage + "ie=utf-8");

        String metaMessageRe = "\\{&quot;id&quot;:(\\d*),&quot;author_name&quot;:&quot;(.*)&quot;,&quot;first_post_id&quot;:(\\d*),&quot;reply_num&quot;:(\\d*),&quot;is_bakan&quot;:(.*),&quot;vid&quot;:&quot;(.*)&quot;,&quot;is_good&quot;:(.*),&quot;is_top&quot;:(.*),&quot;is_protal&quot;:(.*),&quot;is_membertop&quot;:(.*),&quot;is_multi_forum&quot;:(.*),&quot;frs_tpoint&quot;:(.*)\\}?";
        Pattern metaMessagePa = Pattern.compile(metaMessageRe);

        String titleRe = "target=\"_blank\" class=\"j_th_tit \">(.*)</a>$?";
        Pattern titlePa = Pattern.compile(titleRe);


        ArrayList<String[]> metaMessage = analysis(homePageURL, metaMessagePa, titlePa);

        ArrayList<String> result = new ArrayList<>();

        //构建帖子ID List作为返回结果
        for(int i = 0; i < metaMessage.size(); i++){
            result.add(metaMessage.get(i)[0]);
            /*//结果测试
            System.out.print("nindex:  " + i + "   ");
            for(int j = 0; j < metaMessage.get(0).length; j++){
                System.out.print( j + "  " + metaMessage.get(i)[j] + "    ");
            }*/
        }

        return result;
    }


    /**
     * XXX 性能优化，将分散的结果集复制合成结果效率太过低下
     * 通过传入的正则表达式列表完成内容提取
     * @param  String  html          网页文本
     * @param  Pattern ...           paList        正则表达式列表
     * @return         匹配结果
     */
    static ArrayList<String[]> analysis(String html, Pattern ... paList){
            ArrayList<String[]> result = new ArrayList<>();
            ArrayList< ArrayList<String[]> > tmpResult = new ArrayList<>();
            int length = 0;
            //匹配结果
            for(int i = 0; i < paList.length; i++){
                //生成matcher
                Matcher ma = paList[i].matcher(html);
                String[] tmp = new String[ma.groupCount()];
                tmpResult.add(new ArrayList<String[]> ());//为暂存结果的数组增加一个元素

                while(ma.find()) {
                    tmp = new String[ma.groupCount()];
                    //提取帖子信息
                    for(int j = 0; j <ma.groupCount(); j++){
                        tmp[j] = ma.group(j + 1);
                    }

                    tmpResult.get(i).add(tmp);//暂存到此正则表达式对应的结果数组
                }
                length += ma.groupCount();//记录所有正则表达式匹配的捕获组数量
            }

            //合成结果
            String[] tmp = new String[length];
            int sit = 0;
            for(int i = 0; i < tmpResult.get(0).size(); i++){
                tmp = new String[length];
                sit = 0;
                for(int j = 0; j < paList.length; j++){
                    System.arraycopy(tmpResult.get(j).get(i), 0, tmp, sit, tmpResult.get(j).get(i).length);//将第j个正则表达式的第i组匹配结果复制到结果数组中
                    sit +=  tmpResult.get(j).get(i).length;
                }
                result.add(tmp);//将所有正则表达式的结果的合成数组添加到结果中
            }

            return result;
    }



}
