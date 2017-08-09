import java.util.ArrayList;
import java.util.Properties;
import java.io.*;
import java.sql.*;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

class Database {

    static Properties props;
    static String driver;
    static String url;
    static String user;
    static String pass;
    static Connection conn;
    static Statement stmt;



    /**
     * 静态初始化快，用于在类加载时建立数据库连接
     */
    static {
        try{
            props = new Properties();
            props.load(new FileInputStream("Mysql.ini"));
            driver = props.getProperty("driver");
            url = props.getProperty("url");
            user = props.getProperty("user");
            pass = props.getProperty("pass");
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pass);
            stmt = conn.createStatement();
            System.out.println("数据库初始化成功.......");
        }
        catch(FileNotFoundException e){
            System.out.println("找不到配置文件 Mysql.ini");
            e.printStackTrace();
        }
        catch(IOException e){
            System.out.println("配置文件读取异常");
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            System.out.println("数据库驱动载入异常");
            e.printStackTrace();
        }
        catch(SQLException e){
            System.out.println("数据库建立连接失败");
            e.printStackTrace();
        }


    }


    /**
     *  保存主题帖(Therad)的信息到数据库
     * @param ArrayList<String[]> thread 主题帖(Thrad)的各种信息
     */
    static boolean insertThread(String[] thread){
        boolean result = true;
        String sql =
                "INSERT INTO thread " +
                "(tieba_name, " +
                "thread_id, " +
                "title, " +
                "first_post_id, " +
                "user_id, " +
                "data, " +
                "update_time,  " +
                "is_bakan,  " +
                "vid,  " +
                "is_good,  " +
                "is_top,  " +
                "is_protal,  " +
                "is_membertop,  " +
                "is_multi_forum,  " +
                "frs_tpiont ) " +
                "VALUES ( " +
                " '" + thread[0] + "', " +//tieba_name     CHAR(64)  NOT NULL
                thread[1] + ", " +//         thread_id      BIGINT    NOT NULL
                " '" + thread[2].replaceAll("'", "\\\\'") + "', " +//title          CHAR(64)  NOT NULL
                thread[3] + ", " +//         first_post_id  BIGINT    NOT NULL
                thread[4] + ", " +//         user_id        BIGINT    NOT NULL
                " '" + thread[5] + "', " +//     data           DATETIME  NULL
                " '" + thread[6] + "', " +// update_time    DATETIME  NOT NULL
                " '" + thread[7] + "', " +//     is_bakan       CHAR(255) NULL
                " '" + thread[8] + "', " +//     vid            CHAR(255) NULL
                thread[9] + ", " +//          is_good        BOOLEAN   NULL
                thread[10] + ", " +//is_top         BOOLEAN   NULL
                " '" + thread[11] + "', " +//is_protal      CHAR(255) NULL
                " '" + thread[12] + "', " +//is_membertop   CHAR(255) NULL
                " '" + thread[13] + "', " +//is_multi_forum CHAR(255) NULL
                " '" + thread[14] + "' " +//frs_tpiont     CHAR(255) NULL
                " );" ;

        try{
            Database.stmt.executeUpdate(sql);
        }
        catch(MySQLIntegrityConstraintViolationException e){
            //System.out.println("此记录已存在，自动跳过.....");
            result = false;
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("INSERT 失败");
        }

        /*//测试爬取到的Thread信息
        for(int j = 0; j < thread.length; j++){
            System.out.print(thread[j] + "\t");
        }
        System.out.print("\n");*/

        return result;
    }

    /**
     * TODO 内部方法，用于在合适的时候查询数据库，补上Thread信息的中的data一项
     */
    private void addThreadData(){}

    /**
     * 保存帖子信息以及帖子内容到数据库
     * @param ArrayList<String[]> post 帖子信息已经帖子内容
     */
    static void insertPost(ArrayList<String[]> post){
        String sql = "";

            for(int i = 0; i < post.size(); i++){
                sql = "INSERT INTO post " +
                "(tieba_name, " +
                "thread_id, " +
                "post_id, " +
                "user_id, " +
                "content, " +
                "post_no, " +
                "post_index, " +
                "data, " +
                "is_anonym, " +
                "open_id, " +
                "open_type, " +
                "vote_crypt, " +
                "type, " +
                "ptype, " +
                "is_saveface, " +
                "props, " +
                "pb_tpoint) " +
                "VALUES ( " +
                " '" + post.get(i)[0] + "', " +
                post.get(i)[1] + ", " +
                post.get(i)[2] + ", " +
                post.get(i)[3] + ", " +
                " '" + post.get(i)[4].replaceAll("'", "\\\\'") + "', " +
                post.get(i)[5] + ", " +
                post.get(i)[6] + ", " +
                " '" + post.get(i)[7] + "', " +
                post.get(i)[8] + ", " +
                " '" + post.get(i)[9] + "', " +
                " '" + post.get(i)[10] + "', " +
                " '" + post.get(i)[11] + "', " +
                " '" + post.get(i)[12] + "', " +
                " '" + post.get(i)[13] + "', " +
                post.get(i)[14] + ", " +
                " '" + post.get(i)[15] + "', " +
                " '" + post.get(i)[16] + "' " +
                " ) ;";

                try{
                    Database.stmt.executeUpdate(sql);
                }
                catch(MySQLIntegrityConstraintViolationException e){
                    //System.out.println("此记录已存在，自动跳过.....");
                }
                catch(SQLException e){
                    e.printStackTrace();
                    System.out.println("INSERT 失败");
                }

            }


        /*//stmt.executeUpdate(sql);
        for(int i = 0; i < post.size(); i++){
            for(int j = 0; j < post.get(0).length; j++){
                System.out.print(post.get(i)[j] + "\t");
            }
            System.out.print("\n");
        }*/

    }

    /**
     * 保存帖子回复信息到数据库
     * @param ArrayList<String[]> comment 帖子回复信息
     */
    static void insertComment(ArrayList<String[]> comment){
        String sql = "";

        for(int i = 0; i < comment.size(); i++){
            sql =
                "INSERT INTO comment " +
                "(tieba_name, " +
                "post_id, " +
                "comment_id, " +
                "user_id, " +
                "now_time, " +
                "content, " +
                "ptype,  " +
                "during_time,  " +
                "direct_user_id ) " +
                "VALUES ( " +
                " '" + comment.get(i)[0] + "', " +
                comment.get(i)[1] + ", " +
                comment.get(i)[2] + ", " +
                comment.get(i)[3] + ", " +
                " '" + comment.get(i)[4] + "', " +
                " '" + comment.get(i)[5].replaceAll("'", "\\\\'") + "', " +
                " '" + comment.get(i)[6] + "', " +
                " '" + comment.get(i)[7] + "', " +
                comment.get(i)[8] +
                " ) ;";
                try{
                    Database.stmt.executeUpdate(sql);
                }
                catch(MySQLIntegrityConstraintViolationException e){
                    //System.out.println("此记录已存在，自动跳过.....");
                }
                catch(SQLException e){
                    e.printStackTrace();
                    System.out.println("INSERT 失败");
                }

            /*//测试爬取到评论内容
            for(int j = 0; j < comment.get(0).length; j++){
                System.out.print(comment.get(i)[j] + "\t");
            }
            System.out.print("\n");*/
        }
    }

    /**
     * 保存用户信息到数据库
     * @param Strin[] user 用户信息
     */
    static void insertUser(ArrayList<String[]> user){
        String sql = "";

        for(int i = 0; i < user.size(); i++){
            sql =
                "INSERT INTO user_basic " +
                "(user_id, " +
                "user_name, " +
                "user_sex, " +
                "user_create_time ) " +
                "VALUES (" +
                user.get(i)[0] + ", " +
                " '" + user.get(i)[1] + "', " +
                user.get(i)[2] + ", " +
                " '" + user.get(i)[3] + "' " +
                " ) ;";
            try{
                Database.stmt.executeUpdate(sql);
            }
            catch(MySQLIntegrityConstraintViolationException e){
                //System.out.println("此记录已存在，自动跳过.....");
            }
            catch(SQLException e){
                e.printStackTrace();
                System.out.println("INSERT 失败");
            }
        }
    }
}
