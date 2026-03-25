package com.ultraman.themes.model

object SongRepository {

    val songs: List<UltramanSong> = listOf(
        // ── Ultraman (1966) ──
        UltramanSong(1,  "Ultraman no Uta",          "Mitsuko Horie",       "Ultraman",            1966, "01_ultraman_no_uta.mp3",      "#B71C1C", "Opening"),
        UltramanSong(33, "Susume! Ultraman",          "Mitsuko Horie",       "Ultraman",            1966, "33_ultraman_ed.mp3",          "#B71C1C", "Ending"),

        // ── Ultraseven (1967) ──
        UltramanSong(2,  "Ultraseven no Uta",         "Michio Mamiya",       "Ultraseven",          1967, "02_ultraseven_no_uta.mp3",    "#1A237E", "Opening"),
        UltramanSong(34, "Ultraseven no Uta (ED)",    "Michio Mamiya",       "Ultraseven",          1967, "34_ultraseven_ed.mp3",        "#1A237E", "Ending"),

        // ── Return of Ultraman (1971) ──
        UltramanSong(3,  "Kaettekita Ultraman",       "Jirou Dan",           "Return of Ultraman",  1971, "03_kaettekita_ultraman.mp3",  "#880E4F", "Opening"),
        UltramanSong(35, "Ultraman Jack Theme",       "Jirou Dan",           "Return of Ultraman",  1971, "35_kaettekita_ed.mp3",        "#880E4F", "Ending"),

        // ── Ultraman Ace (1972) ──
        UltramanSong(4,  "Ultraman Ace",              "Katsuhiko Kobayashi", "Ultraman Ace",        1972, "04_ultraman_ace.mp3",         "#4A148C", "Opening"),
        UltramanSong(36, "Ultraman Ace Ending",       "Katsuhiko Kobayashi", "Ultraman Ace",        1972, "36_ultraman_ace_ed.mp3",      "#4A148C", "Ending"),

        // ── Ultraman Taro (1973) ──
        UltramanSong(5,  "Ultraman Taro",             "Saburo Kitajima",     "Ultraman Taro",       1973, "05_ultraman_taro.mp3",        "#E65100", "Opening"),
        UltramanSong(37, "Ultraman Taro Ending",      "Saburo Kitajima",     "Ultraman Taro",       1973, "37_ultraman_taro_ed.mp3",     "#E65100", "Ending"),

        // ── Ultraman Leo (1974) ──
        UltramanSong(6,  "Fly! Ultraman Leo",         "Ryuu Manatsu",        "Ultraman Leo",        1974, "06_ultraman_leo.mp3",         "#1B5E20", "Opening"),
        UltramanSong(38, "Ultraman Leo Ending",       "Ryuu Manatsu",        "Ultraman Leo",        1974, "38_ultraman_leo_ed.mp3",      "#1B5E20", "Ending"),

        // ── Ultraman 80 (1980) ──
        UltramanSong(7,  "Ultraman 80",               "TALIZMAN",            "Ultraman 80",         1980, "07_ultraman_80.mp3",          "#F57F17", "Opening"),
        UltramanSong(39, "Ultraman 80 Ending",        "TALIZMAN",            "Ultraman 80",         1980, "39_ultraman_80_ed.mp3",       "#F57F17", "Ending"),

        // ── Ultraman Great (1990) ──
        UltramanSong(8,  "Bokura no Great",           "Masaki Kyoumoto",     "Ultraman Great",      1990, "08_ultraman_great.mp3",       "#006064", "Opening"),

        // ── Ultraman Tiga (1996) ──
        UltramanSong(9,  "Take Me Higher",            "V6",                  "Ultraman Tiga",       1996, "09_take_me_higher.mp3",       "#0D47A1", "Opening"),
        UltramanSong(10, "Brave Love, TIGA",          "Chikyuu Bouei Dan",   "Ultraman Tiga",       1996, "10_brave_love_tiga.mp3",      "#1565C0", "Ending"),
        UltramanSong(40, "Brave Love, TIGA (Full)",   "Chikyuu Bouei Dan",   "Ultraman Tiga",       1996, "40_ultraman_tiga_ed.mp3",     "#1565C0", "Ending"),

        // ── Ultraman Dyna (1997) ──
        UltramanSong(11, "Ultraman Dyna",             "Tatsuya Maeda",       "Ultraman Dyna",       1997, "11_ultraman_dyna.mp3",        "#6A1B9A", "Opening"),
        UltramanSong(41, "Ultraman Dyna Ending",      "Tatsuya Maeda",       "Ultraman Dyna",       1997, "41_ultraman_dyna_ed.mp3",     "#6A1B9A", "Ending"),

        // ── Ultraman Gaia (1998) ──
        UltramanSong(12, "Ultraman Gaia!",            "Masayuki Tanaka",     "Ultraman Gaia",       1998, "12_ultraman_gaia.mp3",        "#00695C", "Opening"),
        UltramanSong(42, "Lovin You Lovin Me",        "B.B. Waves",          "Ultraman Gaia",       1998, "42_ultraman_gaia_ed.mp3",     "#00695C", "Ending"),

        // ── Ultraman Cosmos (2001) ──
        UltramanSong(13, "Spirit",                    "Project D.M.M.",      "Ultraman Cosmos",     2001, "13_ultraman_cosmos.mp3",      "#01579B", "Opening"),
        UltramanSong(32, "Kimi ni Dekiru Nani ka",    "Project D.M.M.",      "Ultraman Cosmos",     2001, "32_ultraman_cosmos_ed.mp3",   "#01579B", "Ending"),
        UltramanSong(59, "Ultraman Cosmos Ending 2",  "Project D.M.M.",      "Ultraman Cosmos",     2001, "59_ultraman_cosmos_ed_2.mp3", "#01579B", "Ending"),

        // ── Ultraman: The Next (2004) ──
        UltramanSong(14, "Ultra Man Next",            "Yuji Shimomura",      "Ultraman: The Next",  2004, "14_ultraman_the_next.mp3",    "#37474F", "Opening"),

        // ── Ultraman Nexus (2004) ──
        UltramanSong(15, "Ultraman Nexus",            "Koji Kikkawa",        "Ultraman Nexus",      2004, "15_ultraman_nexus.mp3",       "#212121", "Opening"),
        UltramanSong(43, "Fight The Future",          "Project D.M.M.",      "Ultraman Nexus",      2004, "43_ultraman_nexus_ed.mp3",    "#212121", "Ending"),

        // ── Ultraman Max (2005) ──
        UltramanSong(16, "Ultraman Max",              "Project D.M.M.",      "Ultraman Max",        2005, "16_ultraman_max.mp3",         "#B71C1C", "Opening"),
        UltramanSong(44, "Ultraman Max Ending",       "Project D.M.M.",      "Ultraman Max",        2005, "44_ultraman_max_ed.mp3",      "#B71C1C", "Ending"),

        // ── Ultraman Mebius (2006) ──
        UltramanSong(17, "Ultraman Mebius",           "Voyager",             "Ultraman Mebius",     2006, "17_ultraman_mebius.mp3",      "#1A237E", "Opening"),
        UltramanSong(45, "Ultraman Mebius Ending",    "Voyager",             "Ultraman Mebius",     2006, "45_ultraman_mebius_ed.mp3",   "#1A237E", "Ending"),

        // ── Ultraman Zero (2010) ──
        UltramanSong(18, "Ultraman Zero",             "Voyager",             "Ultraman Zero",       2010, "18_ultraman_zero.mp3",        "#0D47A1", "Opening"),
        UltramanSong(46, "Atarashii Hikari",          "Voyager",             "Ultraman Zero",       2010, "46_ultraman_zero_ed.mp3",     "#0D47A1", "Ending"),

        // ── Ultraman Saga (2012) ──
        UltramanSong(19, "Ultraman Saga",             "Voyager",             "Ultraman Saga",       2012, "19_ultraman_saga.mp3",        "#1B5E20", "Opening"),

        // ── Ultraman Ginga (2013) ──
        UltramanSong(20, "Ultraman Ginga",            "Voyager",             "Ultraman Ginga",      2013, "20_ultraman_ginga.mp3",       "#4A148C", "Opening"),
        UltramanSong(47, "Starlight",                 "Ultra Super Express",  "Ultraman Ginga",     2013, "47_ultraman_ginga_ed.mp3",    "#4A148C", "Ending"),

        // ── Ultraman Ginga S (2014) ──
        UltramanSong(21, "Ultraman Ginga S",          "Voyager",             "Ultraman Ginga S",    2014, "21_ultraman_ginga_s.mp3",     "#880E4F", "Opening"),
        UltramanSong(48, "Ultraman Ginga S Ending",   "Voyager",             "Ultraman Ginga S",    2014, "48_ultraman_ginga_s_ed.mp3",  "#880E4F", "Ending"),

        // ── Ultraman X (2015) ──
        UltramanSong(22, "Ultraman X",                "Voyager",             "Ultraman X",          2015, "22_ultraman_x.mp3",           "#E65100", "Opening"),
        UltramanSong(49, "Unite",                     "Voyager",             "Ultraman X",          2015, "49_ultraman_x_ed.mp3",        "#E65100", "Ending"),

        // ── Ultraman Orb (2016) ──
        UltramanSong(23, "Ultraman Orb",              "Voyager",             "Ultraman Orb",        2016, "23_ultraman_orb.mp3",         "#006064", "Opening"),
        UltramanSong(50, "Ultraman Orb Ending",       "Voyager",             "Ultraman Orb",        2016, "50_ultraman_orb_ed.mp3",      "#006064", "Ending"),

        // ── Ultraman Geed (2017) ──
        UltramanSong(24, "Ultraman Geed",             "Voyager",             "Ultraman Geed",       2017, "24_ultraman_geed.mp3",        "#F57F17", "Opening"),
        UltramanSong(51, "Kibou no Kakera",           "Voyager",             "Ultraman Geed",       2017, "51_ultraman_geed_ed.mp3",     "#F57F17", "Ending"),

        // ── Ultraman R/B (2018) ──
        UltramanSong(25, "Ultraman R/B",              "Voyager",             "Ultraman R/B",        2018, "25_ultraman_rb.mp3",          "#6A1B9A", "Opening"),
        UltramanSong(52, "Dream Flight!",             "Voyager",             "Ultraman R/B",        2018, "52_ultraman_rb_ed.mp3",       "#6A1B9A", "Ending"),

        // ── Ultraman Taiga (2019) ──
        UltramanSong(26, "Ultraman Taiga",            "Voyager",             "Ultraman Taiga",      2019, "26_ultraman_taiga.mp3",       "#00695C", "Opening"),
        UltramanSong(53, "Sign",                      "Voyager",             "Ultraman Taiga",      2019, "53_ultraman_taiga_ed.mp3",    "#00695C", "Ending"),

        // ── Ultraman Z (2020) ──
        UltramanSong(27, "Ultraman Z",                "Voyager",             "Ultraman Z",          2020, "27_ultraman_z.mp3",           "#0D47A1", "Opening"),
        UltramanSong(54, "Promise for the Future",    "Tasuku Hatanaka",     "Ultraman Z",          2020, "54_ultraman_z_ed.mp3",        "#0D47A1", "Ending"),

        // ── Ultraman Trigger (2021) ──
        UltramanSong(28, "Ultraman Trigger",          "Voyager",             "Ultraman Trigger",    2021, "28_ultraman_trigger.mp3",     "#37474F", "Opening"),
        UltramanSong(55, "Nanairo no Tane",           "Voyager",             "Ultraman Trigger",    2021, "55_ultraman_trigger_ed.mp3",  "#37474F", "Ending"),

        // ── Ultraman Decker (2022) ──
        UltramanSong(29, "Ultraman Decker",           "Voyager",             "Ultraman Decker",     2022, "29_ultraman_decker.mp3",      "#B71C1C", "Opening"),
        UltramanSong(56, "Kanata Title",              "Voyager",             "Ultraman Decker",     2022, "56_ultraman_decker_ed.mp3",   "#B71C1C", "Ending"),

        // ── Ultraman Blazar (2023) ──
        UltramanSong(30, "Ultraman Blazar",           "Voyager",             "Ultraman Blazar",     2023, "30_ultraman_blazar.mp3",      "#1A237E", "Opening"),
        UltramanSong(57, "BLACK STAR",                "MindaRyn",            "Ultraman Blazar",     2023, "57_ultraman_blazar_ed.mp3",   "#1A237E", "Ending"),

        // ── Ultraman Arc (2024) ──
        UltramanSong(31, "Ultraman Arc",              "Voyager",             "Ultraman Arc",        2024, "31_ultraman_arc.mp3",         "#4A148C", "Opening"),
        UltramanSong(58, "Mera Mera",                 "Voyager",             "Ultraman Arc",        2024, "58_ultraman_arc_ed.mp3",      "#4A148C", "Ending")
    )

    val characters: List<UltramanCharacter> by lazy {
        songs.groupBy { it.series }.map { (series, seriesSongs) ->
            UltramanCharacter(
                name   = series,
                series = series,
                year   = seriesSongs.first().year,
                color  = seriesSongs.first().color,
                songs  = seriesSongs
            )
        }.sortedBy { it.year }
    }

    fun findSongById(id: Int): UltramanSong? = songs.find { it.id == id }
}