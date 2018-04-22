
public class Start {

    public static void main(String[] args) throws Exception {

        new ThreadIDFactory("贴吧名称").produceID();

    }

}

/**
 * 贴吧爬虫本阶段的开发任务基本已经完成，
 * 程序已经具备了包括爬取网页，分析网页，存储到数据到数据库等一系列功能。
 * 本程序暂时只能识别 http 类型的帖子，对与 https 类型的帖子，暂时还没有识别能力。
 *
 */
