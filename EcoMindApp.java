package EcoMind;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

public class EcoMindApp {

    // ---------------------------------------------------------
    // DATA STORED FOR THE CURRENT SESSION
    // ---------------------------------------------------------

    static int mood = 0;
    static int ecoScore = 0;
    // 🌱 EcoMind Plant
    static final Path DATA_FILE = Paths.get("ecomind_data.txt");

    static int plantGrowth = 0;
    static int daysNurtured = 0;
    static String lastInteraction = "";
    static int existentialScore = 0;
    static String mainConcern = "Not completed yet";
    static String selectedValue = "Not selected";
    static String reflection = "";


    // ---------------------------------------------------------
    // MAIN METHOD
    // ---------------------------------------------------------

    public static void main(String[] args) throws Exception {

        loadPlantData();

        int port = Integer.parseInt(
        System.getenv().getOrDefault("PORT", "8080")
);

HttpServer server = HttpServer.create(
        new InetSocketAddress("0.0.0.0", port), 0
);

        server.createContext("/", EcoMindApp::homePage);
        server.createContext("/checkin", EcoMindApp::checkInPage);
        server.createContext("/submit", EcoMindApp::submitCheckIn);
        server.createContext("/toolkit", EcoMindApp::toolkitPage);
        server.createContext("/meaning", EcoMindApp::meaningPage);
        server.createContext("/action", EcoMindApp::actionPage);
        server.createContext("/dashboard", EcoMindApp::dashboardPage);

        server.setExecutor(null);

        System.out.println("-----------------------------------------");
        System.out.println("        EC0-MIND APPLICATION");
        System.out.println("-----------------------------------------");
        System.out.println("Server started!");
        System.out.println("Open: http://localhost:8080");
        System.out.println("-----------------------------------------");

        server.start();
    }

    static void loadPlantData() {
    try {
        if (Files.exists(DATA_FILE)) {

            Map<String, String> data = new HashMap<>();

            for (String line : Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8)) {
                String[] parts = line.split("=", 2);

                if (parts.length == 2) {
                    data.put(parts[0], parts[1]);
                }
            }

            plantGrowth = Integer.parseInt(
                    data.getOrDefault("plantGrowth", "0")
            );

            daysNurtured = Integer.parseInt(
                    data.getOrDefault("daysNurtured", "0")
            );

            lastInteraction = data.getOrDefault(
                    "lastInteraction", ""
            );
        }

    } catch (Exception e) {
        System.out.println("Could not load plant data.");
    }
}


static void savePlantData() {
    try {

        String data =
                "plantGrowth=" + plantGrowth + "\n" +
                "daysNurtured=" + daysNurtured + "\n" +
                "lastInteraction=" + lastInteraction + "\n";

        Files.writeString(
                DATA_FILE,
                data,
                StandardCharsets.UTF_8
        );

    } catch (Exception e) {
        System.out.println("Could not save plant data.");
    }
}

static void dailyInteraction(int points) {

    String today = LocalDate.now().toString();

    // Only grow once per day
    if (!today.equals(lastInteraction)) {

        plantGrowth += points;

        if (plantGrowth > 100) {
            plantGrowth = 100;
        }

        daysNurtured++;

        lastInteraction = today;

        savePlantData();
    }
}

static String getPlantEmoji() {

    if (plantGrowth == 0) {
        return "🌰";
    } else if (plantGrowth < 20) {
        return "🌱";
    } else if (plantGrowth < 40) {
        return "🌿";
    } else if (plantGrowth < 60) {
        return "🪴";
    } else if (plantGrowth < 80) {
        return "🌳";
    } else {
        return "🌸🌳";
    }
}
static String getPlantMessage() {

    if (plantGrowth == 0) {
        return "A tiny seed is waiting for your first interaction.";
    } else if (plantGrowth < 20) {
        return "Your little seedling has started to grow. 🌱";
    } else if (plantGrowth < 40) {
        return "Your plant is getting stronger with every mindful day. 🌿";
    } else if (plantGrowth < 60) {
        return "Look at you! Your EcoMind plant is thriving. 🪴";
    } else if (plantGrowth < 80) {
        return "You've nurtured something beautiful. Keep going! 🌳";
    } else {
        return "Your EcoMind journey has bloomed. 🌸";
    }
}

    // ---------------------------------------------------------
    // COMMON HTML START
    // ---------------------------------------------------------

    static String header(String title) {

        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport"
                  content="width=device-width, initial-scale=1.0">

            <title>%s</title>

            <style>

                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                    font-family: Arial, Helvetica, sans-serif;
                }

                body {
                    background: #f4f8f5;
                    color: #20352a;
                    min-height: 100vh;
                }

                nav {
                    background: #ffffff;
                    padding: 18px 8%%;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    border-bottom: 1px solid #dce8df;
                    position: sticky;
                    top: 0;
                    z-index: 10;
                }

                .logo {
                    font-size: 24px;
                    font-weight: bold;
                    color: #2d6a4f;
                }

                .logo span {
                    color: #74a57f;
                }

                nav a {
                    text-decoration: none;
                    color: #426052;
                    margin-left: 22px;
                    font-size: 14px;
                }

                nav a:hover {
                    color: #2d6a4f;
                }

                .container {
                    width: 84%%;
                    max-width: 1100px;
                    margin: auto;
                }

                .hero {
                    padding: 80px 0;
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 50px;
                    align-items: center;
                }

                .hero h1 {
                    font-size: 52px;
                    line-height: 1.08;
                    margin-bottom: 20px;
                    color: #214e38;
                }

                .hero p {
                    color: #60756a;
                    font-size: 18px;
                    line-height: 1.7;
                    margin-bottom: 28px;
                }

                .hero-card {
                    background: linear-gradient(
                        145deg,
                        #dcefe2,
                        #eef7f0
                    );
                    padding: 45px;
                    border-radius: 30px;
                    text-align: center;
                }

                .earth {
                    font-size: 100px;
                    margin-bottom: 15px;
                }

                .btn {
                    display: inline-block;
                    background: #2d6a4f;
                    color: white;
                    padding: 14px 24px;
                    border-radius: 12px;
                    text-decoration: none;
                    border: none;
                    cursor: pointer;
                    font-size: 15px;
                    transition: 0.2s;
                }

                .btn:hover {
                    background: #24563f;
                    transform: translateY(-2px);
                }

                .btn.secondary {
                    background: #e4efe7;
                    color: #2d6a4f;
                }

                .section {
                    padding: 50px 0;
                }

                .section-title {
                    text-align: center;
                    margin-bottom: 35px;
                }

                .section-title h2 {
                    font-size: 32px;
                    color: #214e38;
                    margin-bottom: 10px;
                }

                .section-title p {
                    color: #718278;
                }

                .cards {
                    display: grid;
                    grid-template-columns:
                        repeat(auto-fit, minmax(220px, 1fr));
                    gap: 20px;
                }

                .card {
                    background: white;
                    padding: 28px;
                    border-radius: 20px;
                    border: 1px solid #dfebe2;
                    box-shadow: 0 8px 25px rgba(40, 70, 50, 0.05);
                }

                .card .icon {
                    font-size: 35px;
                    margin-bottom: 15px;
                }

                .card h3 {
                    margin-bottom: 10px;
                    color: #28563f;
                }

                .card p {
                    color: #6d7c73;
                    line-height: 1.6;
                }

                .checkin {
                    max-width: 760px;
                    margin: 50px auto;
                    background: white;
                    padding: 40px;
                    border-radius: 25px;
                    box-shadow: 0 10px 35px rgba(40,70,50,0.07);
                }

                .question {
                    margin-bottom: 35px;
                }

                .question h3 {
                    margin-bottom: 18px;
                    color: #294f3b;
                }

                .options {
                    display: grid;
                    gap: 10px;
                }

                .option {
                    padding: 15px;
                    border: 1px solid #d8e5dc;
                    border-radius: 12px;
                    cursor: pointer;
                    transition: 0.2s;
                }

                .option:hover {
                    background: #eef7f0;
                    border-color: #7da88c;
                }

                .option input {
                    margin-right: 10px;
                }

                textarea {
                    width: 100%%;
                    min-height: 130px;
                    padding: 15px;
                    border: 1px solid #d7e3da;
                    border-radius: 12px;
                    resize: vertical;
                    outline: none;
                }

                textarea:focus {
                    border-color: #5c9270;
                }

                .result {
                    max-width: 700px;
                    margin: 50px auto;
                    background: white;
                    padding: 45px;
                    border-radius: 25px;
                    text-align: center;
                    box-shadow: 0 10px 35px rgba(40,70,50,0.08);
                }

                .score {
                    font-size: 48px;
                    font-weight: bold;
                    color: #2d6a4f;
                    margin: 15px;
                }

                .progress {
                    background: #e8eee9;
                    height: 12px;
                    border-radius: 20px;
                    overflow: hidden;
                    margin: 15px 0 25px;
                }

                .progress-bar {
                    background: #5b9272;
                    height: 100%%;
                    border-radius: 20px;
                }

                .quote {
                    background: #edf6ef;
                    border-left: 5px solid #5b9272;
                    padding: 20px;
                    border-radius: 10px;
                    margin: 25px 0;
                    color: #426052;
                    line-height: 1.6;
                }

                .control-grid {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 25px;
                    margin-top: 30px;
                }

                .control-box {
                    padding: 30px;
                    border-radius: 20px;
                    background: white;
                    border: 1px solid #dfe9e1;
                }

                .control-box h3 {
                    margin-bottom: 18px;
                }

                .control-box ul {
                    padding-left: 20px;
                    line-height: 2;
                    color: #63736a;
                }

                .value-grid {
                    display: grid;
                    grid-template-columns:
                        repeat(auto-fit, minmax(130px, 1fr));
                    gap: 15px;
                    margin-top: 20px;
                }

                .value {
                    background: white;
                    padding: 20px;
                    text-align: center;
                    border: 1px solid #dbe7de;
                    border-radius: 15px;
                    cursor: pointer;
                }

                .value:hover {
                    background: #edf6ef;
                    border-color: #6d9b7b;
                }

                footer {
                    margin-top: 70px;
                    padding: 30px;
                    background: #214e38;
                    color: #d9e8dd;
                    text-align: center;
                }

                @media(max-width: 750px) {

                    nav {
                        padding: 15px 5%%;
                    }

                    nav div:last-child {
                        display: none;
                    }

                    .container {
                        width: 90%%;
                    }

                    .hero {
                        grid-template-columns: 1fr;
                        padding: 50px 0;
                    }

                    .hero h1 {
                        font-size: 40px;
                    }

                    .control-grid {
                        grid-template-columns: 1fr;
                    }

                    .checkin {
                        padding: 25px;
                    }
                }

            </style>
        </head>

        <body>

        <nav>
            <div class="logo">🌱 Eco<span>Mind</span></div>

            <div>
                <a href="/">Home</a>
                <a href="/checkin">Check-in</a>
                <a href="/toolkit">Toolkit</a>
                <a href="/meaning">Meaning</a>
                <a href="/action">Action</a>
                <a href="/dashboard">Dashboard</a>
            </div>
        </nav>
        """.formatted(title);
    }

    // ---------------------------------------------------------
    // FOOTER
    // ---------------------------------------------------------

    static String footer() {

        return """
        <footer>
            <p>🌱 EcoMind</p>
            <p style="margin-top:8px;">
                Understanding feelings. Finding perspective.
                Taking meaningful action.
            </p>
        </footer>

        </body>
        </html>
        """;
    }

    // ---------------------------------------------------------
    // HOME PAGE
    // ---------------------------------------------------------

    static void homePage(HttpExchange exchange) throws IOException {

        String html = header("EcoMind - Eco & Existential Wellbeing");

        html += """
        <main class="container">

            <section class="hero">

                <div>
                    <h1>
                        You don't have to solve
                        the future today.
                    </h1>

                    <p>
                        EcoMind is a reflective wellbeing platform
                        designed to help you understand eco-anxiety,
                        existential concerns, and the uncertainty
                        that comes with thinking about the future.
                    </p>

                    <a class="btn" href="/checkin">
                        Start today's check-in →
                    </a>

                    <a class="btn secondary"
                       href="/toolkit"
                       style="margin-left:8px;">
                        Explore toolkit
                    </a>
                </div>

                <div class="hero-card">

                    <div class="earth">🌍</div>

                    <h2>
                        From overwhelm to agency
                    </h2>

                    <p style="margin-top:12px;color:#60756a;">
                        Understand what you're feeling,
                        focus on what you can influence,
                        and explore what gives your life meaning.
                    </p>

                </div>

            </section>


            <section class="section">

                <div class="section-title">
                    <h2>What can EcoMind help with?</h2>
                    <p>
                        A calm space for reflection and practical action.
                    </p>
                </div>

                <div class="cards">

                    <div class="card">
                        <div class="icon">🌍</div>
                        <h3>Eco-Anxiety</h3>
                        <p>
                            Explore feelings connected with climate
                            change, environmental uncertainty and
                            the future of our planet.
                        </p>
                    </div>

                    <div class="card">
                        <div class="icon">🌀</div>
                        <h3>Overthinking</h3>
                        <p>
                            Use simple grounding activities to
                            step back from overwhelming thoughts.
                        </p>
                    </div>

                    <div class="card">
                        <div class="icon">🌌</div>
                        <h3>Existential Questions</h3>
                        <p>
                            Reflect on purpose, values, uncertainty
                            and what makes life meaningful to you.
                        </p>
                    </div>

                    <div class="card">
                        <div class="icon">🌱</div>
                        <h3>Meaningful Action</h3>
                        <p>
                            Turn concern into small actions within
                            your personal sphere of influence.
                        </p>
                    </div>

                </div>

            </section>


            <section class="section">

                <div class="quote">

                    <strong>Remember:</strong><br><br>

                    Caring about the future does not mean
                    you have to carry the entire future on
                    your shoulders.

                </div>

            </section>

        </main>
        """;

        html += footer();

        sendResponse(exchange, html);
    }

    // ---------------------------------------------------------
    // CHECK-IN PAGE
    // ---------------------------------------------------------

    static void checkInPage(HttpExchange exchange) throws IOException {

        String html = header("EcoMind - Daily Check-in");

        html += """
        <main class="container">

            <div class="checkin">

                <div class="section-title">
                    <h2>🌿 Today's Check-in</h2>
                    <p>
                        Take a moment to notice how you're feeling.
                    </p>
                </div>

                <form method="POST" action="/submit">

                    <div class="question">

                        <h3>1. How are you feeling right now?</h3>

                        <div class="options">

                            <label class="option">
                                <input type="radio"
                                       name="mood"
                                       value="1"
                                       required>
                                😌 Calm
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="mood"
                                       value="2">
                                🙂 Slightly uneasy
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="mood"
                                       value="3">
                                😟 Worried
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="mood"
                                       value="4">
                                😣 Overwhelmed
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="mood"
                                       value="5">
                                😶 Emotionally numb
                            </label>

                        </div>

                    </div>


                    <div class="question">

                        <h3>
                            2. How much have environmental concerns
                            been on your mind?
                        </h3>

                        <div class="options">

                            <label class="option">
                                <input type="radio"
                                       name="eco"
                                       value="1"
                                       required>
                                1 — Very little
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="eco"
                                       value="2">
                                2 — A little
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="eco"
                                       value="3">
                                3 — Moderately
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="eco"
                                       value="4">
                                4 — A lot
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="eco"
                                       value="5">
                                5 — Extremely
                            </label>

                        </div>

                    </div>


                    <div class="question">

                        <h3>
                            3. How much have questions about
                            your future or purpose been on your mind?
                        </h3>

                        <div class="options">

                            <label class="option">
                                <input type="radio"
                                       name="existential"
                                       value="1"
                                       required>
                                1 — Very little
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="existential"
                                       value="2">
                                2 — A little
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="existential"
                                       value="3">
                                3 — Moderately
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="existential"
                                       value="4">
                                4 — A lot
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="existential"
                                       value="5">
                                5 — Extremely
                            </label>

                        </div>

                    </div>


                    <div class="question">

                        <h3>
                            4. What feels most important today?
                        </h3>

                        <div class="options">

                            <label class="option">
                                <input type="radio"
                                       name="concern"
                                       value="Environmental concerns"
                                       required>
                                🌍 Environmental concerns
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="concern"
                                       value="Future uncertainty">
                                🔮 Future uncertainty
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="concern"
                                       value="Purpose and meaning">
                                🌌 Purpose and meaning
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="concern"
                                       value="Studies and career">
                                🎓 Studies and career
                            </label>

                            <label class="option">
                                <input type="radio"
                                       name="concern"
                                       value="Relationships">
                                🤝 Relationships
                            </label>

                        </div>

                    </div>


                    <button class="btn" type="submit">
                        Complete Check-in →
                    </button>

                </form>

            </div>

        </main>
        """;

        html += footer();

        sendResponse(exchange, html);
    }

    // ---------------------------------------------------------
    // PROCESS CHECK-IN
    // ---------------------------------------------------------

    static void submitCheckIn(HttpExchange exchange)
            throws IOException {

                
        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        Map<String, String> data = parseForm(body);

        mood = Integer.parseInt(
                data.getOrDefault("mood", "1")
        );

        ecoScore = Integer.parseInt(
                data.getOrDefault("eco", "1")
        );

        existentialScore = Integer.parseInt(
                data.getOrDefault("existential", "1")
        );

        mainConcern = data.getOrDefault(
                "concern",
                "Not specified"
        );

        dailyInteraction(10);

        String moodText;

        if (mood <= 2) {
            moodText = "You seem relatively settled today.";
        }
        else if (mood == 3) {
            moodText = "You may be carrying some worry today.";
        }
        else {
            moodText =
                "Today seems to feel heavier than usual.";
        }

        String ecoText;

        if (ecoScore <= 2) {
            ecoText = "Your environmental concern appears relatively low today.";
        }
        else if (ecoScore == 3) {
            ecoText = "Environmental concerns are moderately present today.";
        }
        else {
            ecoText = "Environmental concerns seem quite present today.";
        }

        String existentialText;

        if (existentialScore <= 2) {
            existentialText =
                "Questions about purpose and the future are relatively quiet today.";
        }
        else if (existentialScore == 3) {
            existentialText =
                "You seem to be reflecting on some bigger questions today.";
        }
        else {
            existentialText =
                "Questions about purpose, uncertainty or the future seem quite present.";
        }

        String html = header("EcoMind - Check-in Result");

        html += """
        <main class="container">

            <div class="result">

                <div style="font-size:55px;">🌱</div>

                <h1>Check-in Complete</h1>

                <p style="margin-top:10px;color:#6b7d72;">
                    Thanks for taking a moment to check in with yourself.
                </p>

                <div class="quote">
                    %s
                </div>

                <h3>Environmental concern</h3>

                <div class="score">%d / 5</div>

                <div class="progress">
                    <div class="progress-bar"
                         style="width:%d%%"></div>
                </div>

                <p>%s</p>


                <h3 style="margin-top:35px;">
                    Existential concern
                </h3>

                <div class="score">%d / 5</div>

                <div class="progress">
                    <div class="progress-bar"
                         style="width:%d%%"></div>
                </div>

                <p>%s</p>


                <div class="quote">

                    <strong>Today's main concern:</strong><br><br>

                    %s

                </div>


                <a class="btn" href="/toolkit">
                    Explore a tool →
                </a>

                <a class="btn secondary"
                   href="/dashboard"
                   style="margin-left:8px;">
                    View Dashboard
                </a>

            </div>

        </main>
        """.formatted(
                moodText,
                ecoScore,
                ecoScore * 20,
                ecoText,
                existentialScore,
                existentialScore * 20,
                existentialText,
                mainConcern
        );

        html += footer();

        sendResponse(exchange, html);
    }

    // ---------------------------------------------------------
    // TOOLKIT PAGE
    // ---------------------------------------------------------

    static void toolkitPage(HttpExchange exchange)
            throws IOException {

        String html = header("EcoMind - Toolkit");

        html += """
        <main class="container">

            <section class="section">

                <div class="section-title">
                    <h2>🧘 Regulation Toolkit</h2>

                    <p>
                        Choose what feels useful right now.
                    </p>
                </div>


                <div class="cards">

                    <div class="card">

                        <div class="icon">🫁</div>

                        <h3>60-Second Reset</h3>

                        <p>
                            Slow your breathing and bring your
                            attention back to the present moment.
                        </p>

                        <br>

                        <button class="btn"
                                onclick="breathing()">
                            Start
                        </button>

                    </div>


                    <div class="card">

                        <div class="icon">🌍</div>

                        <h3>Control vs Concern</h3>

                        <p>
                            Separate what you can influence from
                            what is outside your control.
                        </p>

                        <br>

                        <a class="btn"
                           href="#control">
                            Explore
                        </a>

                    </div>


                    <div class="card">

                        <div class="icon">📱</div>

                        <h3>News Boundary</h3>

                        <p>
                            Consider setting intentional limits
                            around climate-related content.
                        </p>

                        <br>

                        <button class="btn"
                                onclick="newsTip()">
                            View tip
                        </button>

                    </div>


                    <div class="card">

                        <div class="icon">🧠</div>

                        <h3>Grounding</h3>

                        <p>
                            Notice what you can see, hear and
                            physically feel around you.
                        </p>

                        <br>

                        <button class="btn"
                                onclick="grounding()">
                            Begin
                        </button>

                    </div>

                </div>

            </section>


            <section id="control" class="section">

                <div class="section-title">
                    <h2>🌱 Control vs Concern</h2>

                    <p>
                        You don't have to carry everything.
                    </p>
                </div>


                <div class="control-grid">

                    <div class="control-box">

                        <h3>🌿 Within my influence</h3>

                        <ul>
                            <li>My daily choices</li>
                            <li>How I treat people</li>
                            <li>What I learn</li>
                            <li>My participation in community</li>
                            <li>My response to uncertainty</li>
                            <li>Small environmental actions</li>
                        </ul>

                    </div>


                    <div class="control-box">

                        <h3>🌍 Outside my control</h3>

                        <ul>
                            <li>The entire global climate system</li>
                            <li>Every political decision</li>
                            <li>Other people's choices</li>
                            <li>The complete future</li>
                            <li>Every natural event</li>
                            <li>What everyone thinks</li>
                        </ul>

                    </div>

                </div>

            </section>

        </main>


        <script>

            function breathing() {

                alert(
                    "60-SECOND RESET\\n\\n" +
                    "Breathe in slowly for 4 seconds.\\n" +
                    "Hold gently for 2 seconds.\\n" +
                    "Breathe out slowly for 6 seconds.\\n\\n" +
                    "Repeat several times and notice how your body feels."
                );

            }


            function grounding() {

                alert(
                    "GROUNDING\\n\\n" +
                    "Notice 5 things you can see.\\n" +
                    "Notice 4 things you can touch.\\n" +
                    "Notice 3 things you can hear.\\n" +
                    "Notice 2 things you can smell.\\n" +
                    "Notice 1 thing you appreciate right now."
                );

            }


            function newsTip() {

                alert(
                    "NEWS BOUNDARY\\n\\n" +
                    "Try choosing a specific time to consume " +
                    "environmental news instead of checking it continuously.\\n\\n" +
                    "Being informed does not require constant exposure."
                );

            }

        </script>
        """;

        html += footer();

        sendResponse(exchange, html);
    }

    // ---------------------------------------------------------
    // MEANING PAGE
    // ---------------------------------------------------------

    static void meaningPage(HttpExchange exchange)
            throws IOException {

        String html = header("EcoMind - Meaning & Values");

        html += """
        <main class="container">

            <section class="section">

                <div class="section-title">

                    <h2>🌌 Meaning & Values</h2>

                    <p>
                        There doesn't have to be one universal
                        answer to what makes life meaningful.
                    </p>

                </div>


                <div class="card">

                    <h2>
                        Which values feel important to you?
                    </h2>

                    <p style="margin-top:10px;color:#718278;">
                        Choose something you would like to give
                        more attention to.
                    </p>


                    <div class="value-grid">

                        <div class="value"
                             onclick="selectValue('Learning')">
                            🎓<br><br>
                            Learning
                        </div>

                        <div class="value"
                             onclick="selectValue('Relationships')">
                            ❤️<br><br>
                            Relationships
                        </div>

                        <div class="value"
                             onclick="selectValue('Contribution')">
                            🌱<br><br>
                            Contribution
                        </div>

                        <div class="value"
                             onclick="selectValue('Creativity')">
                            🎨<br><br>
                            Creativity
                        </div>

                        <div class="value"
                             onclick="selectValue('Freedom')">
                            🕊️<br><br>
                            Freedom
                        </div>

                        <div class="value"
                             onclick="selectValue('Community')">
                            🤝<br><br>
                            Community
                        </div>

                    </div>

                </div>


                <div class="card" style="margin-top:25px;">

                    <h2>
                        Reflection prompt
                    </h2>

                    <p style="margin:15px 0;color:#718278;">
                        What makes a day feel meaningful to you,
                        even when the future is uncertain?
                    </p>

                    <textarea id="reflection"
                              placeholder="Write your thoughts here..."></textarea>

                    <br><br>

                    <button class="btn"
                            onclick="saveReflection()">
                        Save Reflection
                    </button>

                    <p id="saved"
                       style="margin-top:15px;color:#4f8062;">
                    </p>

                </div>


                <div class="quote">

                    <strong>A thought to explore:</strong>

                    <br><br>

                    Meaning can come from the things we choose
                    to care about, the people we connect with,
                    the knowledge we pursue, and the actions
                    we take today.

                </div>

            </section>

        </main>


        <script>

            function selectValue(value) {

                alert(
                    "You selected: " + value +
                    "\\n\\nThink about one small way you can "
                    + "express this value today."
                );

            }


            function saveReflection() {

                let text =
                    document.getElementById("reflection").value;

                if (text.trim() === "") {

                    alert("Please write something first.");

                    return;
                }

                document.getElementById("saved").innerText =
                    "✓ Reflection saved for this session.";

            }

        </script>
        """;

        html += footer();

        sendResponse(exchange, html);
    }

    // ---------------------------------------------------------
    // ACTION PAGE
    // ---------------------------------------------------------

    static void actionPage(HttpExchange exchange)
            throws IOException {

        String html = header("EcoMind - Meaningful Action");

        html += """
        <main class="container">

            <section class="section">

                <div class="section-title">

                    <h2>🌱 Turn Concern Into Agency</h2>

                    <p>
                        Choose one small action rather than
                        trying to solve everything.
                    </p>

                </div>


                <div class="cards">

                    <div class="card">

                        <div class="icon">🌳</div>

                        <h3>Connect With Nature</h3>

                        <p>
                            Spend a few minutes outside and
                            notice your surroundings.
                        </p>

                        <br>

                        <button class="btn"
                                onclick="completeAction(this)">
                            Choose
                        </button>

                    </div>


                    <div class="card">

                        <div class="icon">📚</div>

                        <h3>Learn</h3>

                        <p>
                            Read one reliable resource about
                            an environmental topic you care about.
                        </p>

                        <br>

                        <button class="btn"
                                onclick="completeAction(this)">
                            Choose
                        </button>

                    </div>


                    <div class="card">

                        <div class="icon">🚶</div>

                        <h3>Small Daily Choice</h3>

                        <p>
                            Choose one realistic environmentally
                            conscious action today.
                        </p>

                        <br>

                        <button class="btn"
                                onclick="completeAction(this)">
                            Choose
                        </button>

                    </div>


                    <div class="card">

                        <div class="icon">🤝</div>

                        <h3>Community</h3>

                        <p>
                            Talk with someone, join a campus
                            initiative, or participate locally.
                        </p>

                        <br>

                        <button class="btn"
                                onclick="completeAction(this)">
                            Choose
                        </button>

                    </div>

                </div>


                <div class="quote">

                    <strong>
                        Action doesn't have to be huge to be meaningful.
                    </strong>

                    <br><br>

                    The goal is not to remove every environmental
                    problem. The goal is to identify where your
                    own values and influence overlap.

                </div>

            </section>

        </main>


        <script>

            function completeAction(button) {

                button.innerText = "✓ Selected";

                button.style.background = "#5b9272";

                alert(
                    "Nice choice! 🌱\\n\\n" +
                    "You selected one small action within "
                    +
                    "your sphere of influence."
                );

            }

        </script>
        """;

        html += footer();

        sendResponse(exchange, html);
    }

    // ---------------------------------------------------------
    // DASHBOARD
    // ---------------------------------------------------------

    static void dashboardPage(HttpExchange exchange)
            throws IOException {

                String plant = getPlantEmoji();
                String plantMessage = getPlantMessage();
        String html = header("EcoMind - Dashboard");

        html += """
        <main class="container">

            <section class="section">

                <div class="section-title">

                    <h2>📊 Your Reflection Dashboard</h2>

                    <p>
                        A simple snapshot of your latest check-in.
                    </p>

                </div>


                <div class="cards">

                    <div class="card">

                        <div class="icon">🙂</div>

                        <h3>Current Mood</h3>

                        <div class="score">
                            %d / 5
                        </div>

                        <p>
                            Based on your latest check-in.
                        </p>

                    </div>


                    <div class="card">

                        <div class="icon">🌍</div>

                        <h3>Eco Concern</h3>

                        <div class="score">
                            %d / 5
                        </div>

                        <div class="progress">
                            <div class="progress-bar"
                                 style="width:%d%%">
                            </div>
                        </div>

                    </div>


                    <div class="card">

                        <div class="icon">🌌</div>

                        <h3>Existential Concern</h3>

                        <div class="score">
                            %d / 5
                        </div>

                        <div class="progress">
                            <div class="progress-bar"
                                 style="width:%d%%">
                            </div>
                        </div>

                    </div>

                </div>


                <div class="card" style="margin-top:25px;">

                    <h2>Today's main concern</h2>

                    <p style="font-size:20px;
                              margin-top:15px;
                              color:#426052;">
                        %s
                    </p>

                </div>


                <div class="quote">

                    <strong>
                        This dashboard is for reflection,
                        not diagnosis.
                    </strong>

                    <br><br>

                    Your responses represent a snapshot of how
                    you reported feeling at this moment. Feelings
                    can change from day to day.

                </div>

                <div class="card plant-card">

    <div style="font-size: 90px;">
        %s
    </div>

    <h2>Your EcoMind Plant</h2>

    <p>%s</p>

    <div style="
        width: 80%%;
        height: 12px;
        background: #e5e7eb;
        border-radius: 20px;
        margin: 20px auto;
        overflow: hidden;
    ">

        <div style="
            width: %d%%;
            height: 100%%;
            background: #6aaa64;
            border-radius: 20px;
        "></div>

    </div>

    <p>
        <strong>%d%%</strong> grown
        •
        <strong>%d</strong> days nurtured
    </p>

</div>


                <a class="btn" href="/checkin">
                    New Check-in
                </a>

            </section>

        </main>
        """.formatted(
        mood,
        ecoScore,
        ecoScore * 20,
        existentialScore,
        existentialScore * 20,
        mainConcern,
        plant,
        plantMessage,
        plantGrowth,
        plantGrowth,
        daysNurtured
);

        html += footer();

        sendResponse(exchange, html);
    }

    // ---------------------------------------------------------
    // FORM PARSER
    // ---------------------------------------------------------

    static Map<String, String> parseForm(String body) {

        Map<String, String> map = new HashMap<>();

        String[] pairs = body.split("&");

        for (String pair : pairs) {

            String[] parts = pair.split("=", 2);

            if (parts.length == 2) {

                String key = URLDecoder.decode(
                        parts[0],
                        StandardCharsets.UTF_8
                );

                String value = URLDecoder.decode(
                        parts[1],
                        StandardCharsets.UTF_8
                );

                map.put(key, value);
            }
        }

        return map;
    }

    // ---------------------------------------------------------
    // SEND HTML RESPONSE
    // ---------------------------------------------------------

    static void sendResponse(
            HttpExchange exchange,
            String response
    ) throws IOException {

        byte[] bytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders()
                .set("Content-Type", "text/html; charset=UTF-8");

        exchange.sendResponseHeaders(
                200,
                bytes.length
        );

        try (OutputStream output =
                     exchange.getResponseBody()) {

            output.write(bytes);
        }
    }
}
