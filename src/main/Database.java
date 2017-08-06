import java.util.ArrayList;

class Database {

    /**
     * TODO 保存主题帖(Therad)的信息到数据库
     * @param ArrayList<String[]> thread 主题帖(Thrad)的各种信息
     */
    static void insertThread(ArrayList<String[]> thread){
        for(int i = 0; i < thread.size(); i++){
            for(int j = 0; j < thread.get(0).length; j++){
                System.out.print(thread.get(i)[j] + "\t");
            }
            System.out.print("\n");
        }
    }

    /**
     * TODO 内部方法，用于在合适的时候查询数据库，补上Thread信息的中的data一项
     */
    private void addThreadData(){}


    /**
     * TODO 保存帖子信息以及帖子内容到数据库
     * @param ArrayList<String[]> post 帖子信息已经帖子内容
     */
    static void insertPost(ArrayList<String[]> post){
        for(int i = 0; i < post.size(); i++){
            for(int j = 0; j < post.get(0).length; j++){
                System.out.print(post.get(i)[j] + "\t");
            }
            System.out.print("\n");
        }

    }

    /**
     * TODO 保存帖子回复信息到数据库
     * @param ArrayList<String[]> comment 帖子回复信息
     */
    static void insertComment(ArrayList<String[]> comment){
        for(int i = 0; i < comment.size(); i++){
            for(int j = 0; j < comment.get(0).length; j++){
                System.out.print(comment.get(i)[j] + "\t");
            }
            System.out.print("\n");
        }
    }

    /**
     * TODO 保存用户信息到数据库
     * @param Strin[] userMsg 用户信息
     */
    static void insertUser(String[] userMsg){}
}
