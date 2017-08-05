import java.util.ArrayList;
import java.util.regex.*;


class Tool{

    /**
     * 传入帖子网页文件,分析并提取回帖
     * @param  String html          帖子网页文件
     * @return        回帖内容及信息
     */
    static ArrayList<String[]> analysisPost(String post){
        //提取帖子信息的正则表达式
        String messageRe = new String(
        "\\{&quot;author&quot;:\\{&quot;user_id&quot;:(\\d*),&quot;user_name&quot;:&quot;(.*?)&quot;,&quot;name_u&quot;:&quot;(.*?)&quot;,&quot;user_sex&quot;:(\\d),&quot;portrait&quot;:&quot;(.*?)&quot;,&quot;is_like&quot;:(\\d),&quot;level_id&quot;:(\\d*),&quot;level_name&quot;:&quot;(.*?)&quot;,&quot;cur_score&quot;:(\\d*),&quot;bawu&quot;:(\\d*),&quot;props&quot;:(.*?)\\},&quot;content&quot;:\\{&quot;post_id&quot;:(\\d*),&quot;is_anonym&quot;:(.*?),&quot;open_id&quot;:&quot;(.*?)&quot;,&quot;open_type&quot;:&quot;(.*?)&quot;,&quot;date&quot;:&quot;(.*?)&quot;,&quot;vote_crypt&quot;:&quot;(.*?)&quot;,&quot;post_no&quot;:(\\d*),&quot;type&quot;:&quot;(\\d*)&quot;,&quot;comment_num&quot;:(\\d*),&quot;ptype&quot;:&quot;(\\d*)&quot;,&quot;is_saveface&quot;:(.*?),&quot;props&quot;:(.*?),&quot;post_index&quot;:(\\d*),&quot;pb_tpoint&quot;:(.*?)\\}\\}?"
        );
        Pattern messagePa = Pattern.compile(messageRe);

        //提取帖子内容的正则表达式
        String contentRe = new String("j_d_post_content  clearfix\">(.*)</div><br></cc>?");
        Pattern contentPa = Pattern.compile(contentRe);

        return analysis(post, messagePa, contentPa);
    }

    /**
     * 传入贴吧主页文件,返回主题帖信息及ID(ThradID)
     * @param  String html          贴吧主页网页文本文件
     * @return        主题帖信息及ID(ThradID)
     */
    static ArrayList<String[]> analysisThread(String homePage){
        //主题帖(Thread)基本信息
        String metaMessageRe = "\\{&quot;id&quot;:(\\d*),&quot;author_name&quot;:&quot;(.*)&quot;,&quot;first_post_id&quot;:(\\d*),&quot;reply_num&quot;:(\\d*),&quot;is_bakan&quot;:(.*),&quot;vid&quot;:&quot;(.*)&quot;,&quot;is_good&quot;:(.*),&quot;is_top&quot;:(.*),&quot;is_protal&quot;:(.*),&quot;is_membertop&quot;:(.*),&quot;is_multi_forum&quot;:(.*),&quot;frs_tpoint&quot;:(.*)\\}'>?";
        Pattern metaMessagePa = Pattern.compile(metaMessageRe);
        //主题帖(Thread)名称
        String titleRe = "target=\"_blank\" class=\"j_th_tit \">(.*)</a>$?";
        Pattern titlePa = Pattern.compile(titleRe);

        return analysis(homePage, metaMessagePa, titlePa);

    }

    /**
     * 传入帖子网页文件,分析并提取评论
     * @param  String page          包含评论的整个网页文件
     * @return        详细的评论信息
     */
    static ArrayList<String[]> analysisComment(String page){
        ArrayList<String[]> result = new ArrayList<>();
        ArrayList<String[]> tmpResult = new ArrayList<>();


        //提取某个帖子下的所有评论的正则表达式
        String commentRe1 = "\"(\\d*)\":\\[(\\{.*?\\})\\]";
        Pattern commentPa1 = Pattern.compile(commentRe1);

        tmpResult = analysis(page, commentPa1);
        //精确提取所有的评论
        String commentRe2 = "(\\{)\"comment_id\":\"(\\d*)\",\"user_id\":\"(\\d*)\",\"now_time\":(\\d*),\"content\":\"(.*?)\",\"ptype\":(\\d*),\"during_time\":(.*?)\\}";
        Pattern commentPa2 = Pattern.compile(commentRe2);


        ArrayList<String[]> tmp = new ArrayList<>();
        for(int i = 0; i < tmpResult.size(); i++){
            tmp = new ArrayList<>();
            tmp = analysis(tmpResult.get(i)[1], commentPa2);
            for(int j = 0; j < tmp.size(); j++){
                tmp.get(j)[0] = tmpResult.get(i)[0];
                result.add(tmp.get(j));
            }
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
    static private ArrayList<String[]> analysis(String html, Pattern ... paList){
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

    /**
     * 将Unicode原码字符串转换为正常的字符串
     * @param  String unicode       含有unic原码的字符串
     * @return        转换完成的正常字符串
     */
    private static String unicode2Str(String unicode) {
        Pattern re = Pattern.compile("(?<=\\\\u)\\w{4}");
        Matcher ma = re.matcher(unicode);
        while(ma.find()){
            String hex = ma.group();//找到第一个Unicode原码
            int data = Integer.parseInt(hex, 16);
            unicode = unicode.replaceFirst("\\\\u\\w{4}", (char)data + "");//将第一个Unicode原码转码个正常字符
        }
        return unicode;
    }

}
