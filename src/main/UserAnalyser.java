import java.util.ArrayList;

class UserAnalyser {

    /**
     * 接受PostAnalyser 传入的用户信息，并将其传入数据库
     * @param ArrayList<String[]> UserItem   包含用户信息的帖子信息
     * @param String              tieba_name 贴吧名
     */
    static void sendTo(ArrayList<String[]> userItem){
        ArrayList<String[]> formatedData = new ArrayList<>();
        String[] tmp = new String[4];
        for(int i = 0; i < userItem.size(); i++){
            tmp = new String[4];

            tmp[0] = userItem.get(i)[0];
            tmp[1] = Tool.unicode2Str(userItem.get(i)[1]);
            tmp[2] = userItem.get(i)[3];
            tmp[3] = "1990-01-01 00:00:00";

            formatedData.add(tmp);
        }

        Database.insertUser(formatedData);
    }

}
