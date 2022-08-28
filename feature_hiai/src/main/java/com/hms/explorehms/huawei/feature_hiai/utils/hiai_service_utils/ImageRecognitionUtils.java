/*
 *
 *   Copyright 2020. Explore in HMS. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   You may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package com.hms.explorehms.huawei.feature_hiai.utils.hiai_service_utils;

import java.util.HashMap;

public class ImageRecognitionUtils {

    public static final String[] IMAGE_CATEGORY_LABELING_CATEGORY = {
            "People",
            "Food",
            "Landscapes",
            "Documents",
            "Festival",
            "Activities",
            "Animal",
            "Sports",
            "Vehicle",
            "Household products",
            "Appliance",
            "Art",
            "Tools",
            "Apparel",
            "Accessories",
            "Toy"
    };
    public static final HashMap<Integer, String> IMAGE_CATEGORY_LABELING_CONTENTS = new HashMap<>();


    //IMAGE CATEGORY LABELING

    static {
        IMAGE_CATEGORY_LABELING_CONTENTS.put(0, "people");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(1, "food");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(2, "landscapes");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(3, "document");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(4, "id card");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(5, "passport");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(6, "debit card");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(7, "bicycle");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(8, "bus");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(9, "ship");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(10, "train");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(11, "airplane");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(12, "automobile");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(13, "bird");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(14, "cat");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(15, "dog");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(16, "fish");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(18, "wardrobe");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(19, "smartphone");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(20, "laptop");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(24, "bridal veil");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(25, "flower");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(26, "toy block");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(27, "sushi");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(28, "barbecue");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(29, "banana");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(31, "watermelon");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(32, "noodle");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(34, "piano");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(35, "wedding");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(36, "playing chess");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(37, "basketball");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(38, "badminton");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(39, "football");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(40, "city overlook");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(41, "sunrise sunset");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(42, "ocean & beach");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(43, "bridge");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(44, "sky");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(45, "grassland");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(46, "street");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(47, "night");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(49, "grove");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(50, "lake");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(51, "snow");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(52, "mountain");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(53, "building");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(54, "cloud");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(55, "waterfall");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(56, "fog & haze");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(57, "porcelain");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(58, "model runway");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(59, "rainbow");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(60, "candle");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(62, "statue of liberty");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(63, "ppt");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(66, "baby carriage");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(67, "group photo");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(68, "dine together");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(69, "eiffel tower");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(70, "dolphin");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(71, "giraffe");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(72, "penguin");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(73, "tiger");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(74, "zebra");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(76, "lion");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(77, "elephant");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(78, "leopard");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(79, "peafowl");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(80, "blackboard");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(81, "balloon");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(83, "air conditioner");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(84, "washing machine");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(85, "refrigerator");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(86, "camera");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(88, "gun");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(89, "dress & skirt");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(91, "uav");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(92, "apple");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(93, "dumpling");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(94, "coffee");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(95, "grape");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(96, "hot pot");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(97, "diploma");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(102, "watch");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(103, "glasses");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(104, "ferris wheel");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(105, "fountain");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(106, "pavilion");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(107, "fireworks");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(108, "business card");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(109, "riding");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(110, "music show");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(111, "sailboat");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(112, "giant panda");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(113, "birthday cake");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(114, "birthday");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(115, "christmas");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(116, "the great wall");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(117, "oriental pearl tower");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(118, "guangzhou tower");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(120, "tower");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(121, "rabbit");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(123, "trolley case");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(124, "nail");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(125, "guitar");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(128, "yeah");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(129, "swimming");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(130, "riding bicycle");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(131, "jump");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(132, "hug");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(133, "boating"); // TODO https://developer.huawei.com/consumer/en/doc/development/hiai-References/label-detector-0000001054096401
        IMAGE_CATEGORY_LABELING_CONTENTS.put(134, "stonecrop family");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(135, "trousers");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(136, "mahjong");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(137, "handbag");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(138, "ring");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(139, "bracelet");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(140, "make a snowman");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(141, "flag raising ceremony");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(142, "dragon dance");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(143, "lion dance");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(144, "parent-child");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(145, "jellyfish");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(146, "turtle");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(147, "brown bear");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(148, "black bear");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(149, "polar bear");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(150, "ostrich");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(151, "parrot");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(152, "flamingos");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(153, "chicken");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(154, "duck");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(155, "snails");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(156, "butterfly");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(157, "camel");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(158, "rhinoceros");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(159, "cows");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(160, "deer");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(161, "horse");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(162, "hippo");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(163, "peking opera face");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(164, "oil painting");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(165, "calligraphy");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(166, "sketch");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(167, "drum");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(168, "harp");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(169, "flute");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(170, "buddha statue");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(171, "burger king");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(172, "kfc");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(173, "mcdonald");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(174, "pizza hut");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(175, "bra");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(176, "bikini");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(177, "cheongsam");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(178, "beijing opera costume");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(179, "deed");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(180, "account book");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(181, "invoice");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(182, "train ticket");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(183, "air ticket");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(184, "movie ticket");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(185, "certificate");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(186, "banner");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(187, "hi world");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(188, "fu character");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(189, "red envelope");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(190, "spring festival");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(191, "qr code");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(192, "bar code");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(193, "paper money");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(194, "coin");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(195, "form");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(196, "menu");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(197, "map");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(198, "street sign");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(199, "licence plate");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(200, "tablet");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(201, "water heater");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(202, "chinese buns");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(203, "rice");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(204, "porridge");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(205, "toast bread");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(206, "moon cake");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(207, "pizza");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(208, "zongzi");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(209, "beer");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(210, "red wine");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(211, "milk");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(212, "egg");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(213, "hamburger");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(214, "french fries");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(215, "sandwich");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(216, "steak");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(217, "mango");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(218, "peach");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(219, "kiwi");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(220, "pomegranate");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(221, "durian");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(222, "crab");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(223, "lobster");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(224, "crayfish");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(225, "base shrimp");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(226, "pho shrimp");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(227, "oysters");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(228, "scallop");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(229, "abalone");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(230, "onion");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(231, "corn");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(232, "cucumber");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(233, "chinese cabbage");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(234, "cabbage");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(235, "okra");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(236, "carrot");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(237, "white radish");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(238, "potato");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(239, "sweet potato");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(240, "eggplant");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(241, "red chili");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(242, "green pepper");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(243, "vase");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(244, "clock");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(245, "table lamp");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(246, "floor lamp");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(247, "fork");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(248, "spoon");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(249, "plate");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(250, "bowl");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(251, "toilet");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(252, "potala palace in Lhase");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(253, "sophia church of harbin");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(254, "macau ruins of st paul's arch");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(255, "beijing tiananmen");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(256, "beijing national stadium (bird's nest)");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(257, "beijing national swimming center (water cube)");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(258, "chongqing jiefangbei");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(259, "kunming dongsi tower");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(260, "chengdu covered bridge");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(261, "hong kong bank of china tower");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(262, "yurt");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(263, "paris arc de triomphe");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(264, "beijing central television building");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(265, "taipei 101 building");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(266, "suzhou oriental gate");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(267, "bamboo forest");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(268, "windmill");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(269, "lipstick");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(270, "liquid foundation");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(271, "baseball");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(272, "billiards");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(273, "boxing");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(274, "diving");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(275, "fencing");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(276, "ice hockey");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(277, "pingpong");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(278, "volleyball");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(279, "treadmill");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(280, "skate");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(281, "ski");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(282, "high jump");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(283, "long jump");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(284, "pliers");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(285, "scissors");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(286, "ladder");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(287, "spanner");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(288, "binoculars");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(289, "barbie doll");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(290, "toy gun");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(291, "hot air balloon");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(292, "desktop");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(293, "foliage");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(294, "marriage certificate");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(295, "driver licence");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(296, "driving licence");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(297, "shanghai world expo hall");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(298, "taekwondo");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(299, "layout");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(300, "hat");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(301, "goose");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(302, "cow");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(303, "pig");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(304, "sheep");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(305, "underwear");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(306, "suit");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(307, "excavator");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(308, "ocean & beach & lake");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(-1, "unknown");
        IMAGE_CATEGORY_LABELING_CONTENTS.put(-2, "other");


    }

    //SCENE DETECTION
    private ImageRecognitionUtils() {

    }

    /**
     * @param type Scene Type
     * @return Scene Type Name
     */
    public static String getSceneType(int type) {

        String result = "NULL";

        switch (type) {
            case 0:
                result = "UNKNOWN";
                break;
            case 1:
                result = "UNSUPPORT";
                break;
            case 2:
                result = "BEACH";
                break;
            case 3:
                result = "BLUESKY";
                break;
            case 4:
                result = "SUNSET";
                break;
            case 5:
                result = "FOOD";
                break;
            case 6:
                result = "FLOWER";
                break;
            case 7:
                result = "GREENPLANT";
                break;
            case 8:
                result = "SNOW";
                break;
            case 9:
                result = "NIGHT";
                break;
            case 10:
                result = "TEXT";
                break;
            case 11:
                result = "STAGE";
                break;
            case 12:
                result = "CAT";
                break;
            case 13:
                result = "DOG";
                break;
            case 14:
                result = "FIREWORK";
                break;
            case 15:
                result = "OVERCAST";
                break;
            case 16:
                result = "FALLEN";
                break;
            case 17:
                result = "PANDA";
                break;
            case 18:
                result = "CAR";
                break;
            case 19:
                result = "OLDBUILDINGS";
                break;
            case 20:
                result = "BICYCLE";
                break;
            case 21:
                result = "WATERFALL";
                break;
            case 22:
                result = "PLAYGROUND";
                break;
            case 23:
                result = "CORRIDOR";
                break;
            case 24:
                result = "CABIN";
                break;
            case 25:
                result = "WASHROOM";
                break;
            case 26:
                result = "KITCHEN";
                break;
            case 27:
                result = "BEDROOM";
                break;
            case 28:
                result = "DININGROOM";
                break;
            case 29:
                result = "LIVINGROOM";
                break;
            case 30:
                result = "SKYSCRAPER";
                break;
            case 31:
                result = "BRIDGE";
                break;
            case 32:
                result = "WATERSIDE";
                break;
            case 33:
                result = "MOUNTAIN";
                break;
            case 34:
                result = "OVERLOOK";
                break;
            case 35:
                result = "WORKSITE";
                break;
            case 36:
                result = "ISLAMBUILDINGS";
                break;
            case 37:
                result = "EUROPEANBUILDINGS";
                break;
            case 38:
                result = "FOOTBALLCOURT";
                break;
            case 39:
                result = "BASEBALLCOURT";
                break;
            case 40:
                result = "TENNISCOURT";
                break;
            case 41:
                result = "INCAR";
                break;
            case 42:
                result = "BADMINTONCOURT";
                break;
            case 43:
                result = "PINGPONGCOURT";
                break;
            case 44:
                result = "SWIMMINGPOOL";
                break;
            case 45:
                result = "ALPACA";
                break;
            case 46:
                result = "LIBRARY";
                break;
            case 47:
                result = "SUPERMARKET";
                break;
            case 48:
                result = "RESTAURANT";
                break;
            case 49:
                result = "TIGER";
                break;
            case 50:
                result = "PENGUIN";
                break;
            case 51:
                result = "ELEPHANT";
                break;
            case 52:
                result = "DINOSAUR";
                break;
            case 53:
                result = "WATERSURFACE";
                break;
            case 54:
                result = "INDOORBASKETBALLCOURT";
                break;
            case 55:
                result = "BOWLINGALLEY";
                break;
            case 56:
                result = "CLASSROOM";
                break;
            case 57:
                result = "RABBIT";
                break;
            case 58:
                result = "RHINOCEROS";
                break;
            case 59:
                result = "CAMEL";
                break;
            case 60:
                result = "TORTOISE";
                break;
            case 61:
                result = "LEOPARD";
                break;
            case 62:
                result = "GIRAFFE";
                break;
            case 63:
                result = "PEACOCK";
                break;
            case 64:
                result = "KANGAROO";
                break;
            case 65:
                result = "LION";
                break;
            case 66:
                result = "MOTORCYCLE";
                break;
            case 67:
                result = "AIRCRAFT";
                break;
            case 68:
                result = "TRAIN";
                break;
            case 69:
                result = "SHIP";
                break;
            case 70:
                result = "GLASSES";
                break;
            case 71:
                result = "WATCH";
                break;
            case 72:
                result = "HIGHHEELS";
                break;
            case 73:
                result = "WASHINGMACHINE";
                break;
            case 74:
                result = "AIRCONDITIONER";
                break;
            case 75:
                result = "CAMERA";
                break;
            case 76:
                result = "MAP";
                break;
            case 77:
                result = "KEYBOARD";
                break;
            case 78:
                result = "REDENVELOPE";
                break;
            case 79:
                result = "FUCHARACTER";
                break;
            case 80:
                result = "XICHARACTER";
                break;
            case 81:
                result = "DRAGONDANCE";
                break;
            case 82:
                result = "LIONDANCE";
                break;
            case 83:
                result = "GO";
                break;
            case 84:
                result = "TEDDYBEAR";
                break;
            case 85:
                result = "TRANSFORMER";
                break;
            case 86:
                result = "THESMURFS";
                break;
            case 87:
                result = "LITTLEPONY";
                break;
            case 88:
                result = "BUTTERFLY";
                break;
            case 89:
                result = "LADYBUG";
                break;
            case 90:
                result = "DRAGONFLY";
                break;
            case 91:
                result = "BILLIARDROOM";
                break;
            case 92:
                result = "MEETINGROOM";
                break;
            case 93:
                result = "OFFICE";
                break;
            case 94:
                result = "BAR";
                break;
            case 95:
                result = "MALLCOURTYARD";
                break;
            case 96:
                result = "DEER";
                break;
            case 97:
                result = "CATHEDRALHALL";
                break;
            case 98:
                result = "BEE";
                break;
            case 99:
                result = "HELICOPTER";
                break;
            case 100:
                result = "MAHJONG";
                break;
            case 101:
                result = "CHESS";
                break;
            case 102:
                result = "MCDONALDS";
                break;
            case 103:
                result = "ORNAMENTALFISH";
                break;
            case 104:
                result = "WIDEBUILDING";
                break;
            default:
                result = "";
                break;
        }

        return result;
    }
}
