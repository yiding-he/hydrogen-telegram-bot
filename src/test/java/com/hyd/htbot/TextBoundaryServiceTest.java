package com.hyd.htbot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TextBoundaryServiceTest extends HydrogenTelegramBotApplicationTests {

    @Autowired
    private TextBoundaryService textBoundaryService;

    @Test
    void process() {
        var originalText = String.join("\n\n",
                "日本福岛核灾难后，许多国家都暂停修建新核电站的计划，中国也是。不过，如今，14年之后，中国正如火如荼地兴建核电站，目前在建的就有29座。“这比德国历史上拥有的核电站总数还多。按照这一速度，到2030年，中国将超越美国，成为核电站数量最多的国家。核能热潮的背后是什么呢？”",
                "文章引述牛津能源研究所的中国问题专家霍夫（Anders Hove）说，中国有独一无二的社会与政治条件，推动了核电站的兴建。这就包括，那里的民众几乎没有机会反对政府的计划。“在西方，人们还在磋商、示威，在中国已经开始施工了。”此外，对地方领导而言，核电项目获批对仕途大大有利。也就是说，这更多是出于地方领导的自身利益。",
                "文章举例说，海南民众曾长期反对修建风力发电园区。后来，地方领导强力推动核电项目。结果是：自2016年以来，昌江核电站四个反应堆机组各为这一风景如画的省份提供600兆瓦的电力。",
                "霍夫同时指出，对中国来说，核电也被视为火力发电的碳中性替代，以完成该国在习近平治下雄心勃勃的环境目标。至于核废料的最终存储，对中国也是个巨大挑战。尽管该国使用新的理念，将放射性废料熔解在玻璃体内，从而降低危险性。但大部分废料仍按照旧有的方式储存在中国东北的地下深处。",
                "文章写道，在中国，修建核电站的速度快、成本低。此外，文章引述麻省理工大学教授邦乔诺（Jacopo Buongiorno）说，中国对核电研究也投入巨资。目标是，扩大领先地位，中长期效仿俄罗斯出口核电。",
                "不过，迄今为止，仅巴基斯坦愿购买中国反应堆技术。2016年和2021年，中国核电站在巴基斯坦入网。而许多其它国家如苏丹、埃及、南非尽管在多年前与中国签署框架协议，但始终停留在规划阶段。“核能专家邦乔诺表示：‘出口核能会将买家和卖家长期捆绑在一起。’因为这样的交易不单包括建设，往往还包括核电站维护、燃料棒供给。许多国家对于是否愿意与中国长期如此紧密地捆绑在一起有疑虑。”",
                "文章也指出，尽管出现核电热潮，核能目前覆盖中国的电力需求仅约5%，2035年计划达到10%。相形之下，可再生能源已覆盖电力需求的35%。"
        );
        var textBoundary = new TextBoundary();
        textBoundary.setOriginalText(originalText);
        textBoundary.setMaxPixelWidth(500);
        textBoundary.setFont(new Font("Arial", Font.PLAIN, 30));
        textBoundaryService.process(textBoundary);

        for (String s : textBoundary.getSplitText()) {
            System.out.println(s);
        }
    }
}