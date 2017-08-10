import java.net.HttpURLConnection;
import java.net.*;
import java.io.*;

class PageRequester {
    /**
     * 通过帖子URL获得帖子文本
     * @param  String urlStr        帖子URL
     * @return        String格式的帖子HTML源码
     */
    static String getHTML(String urlStr){
        //System.out.println("**request html .....");
        URL url = null;
        String result = "";
        HttpURLConnection conn = null;
        BufferedReader in = null;
        int sleepTime = 15000;

        while(true){
            try{
                url = new URL(urlStr);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
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
            catch(SocketTimeoutException ste){
                //ste.printStackTrace();
                sleepTime = sleepTime * 2;
                System.out.println("请求超时，本线程暂时睡眠" + (sleepTime / 60000.0) + "分钟.....");
                try{
                    Thread.sleep(sleepTime);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }

                continue;

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

            break;
        }



        //System.out.println("**get html!");
        return result;
    }
}
