class Tool{




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
