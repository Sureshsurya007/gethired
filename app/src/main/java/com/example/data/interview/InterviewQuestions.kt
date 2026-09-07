package com.example.data.interview

data class InterviewQuestion(
    val id: String,
    val questionText: String,
    val category: String,
    val difficulty: String = "Entry Level",
    val guidanceTip: String,
    val expectedElements: List<String>,
    val sampleAnswerGood: String,
    val sampleAnswerBrief: String
)

object InterviewQuestionsBank {
    val questions: List<InterviewQuestion> = listOf(
        InterviewQuestion(
            id = "q1_tell_me_about_yourself",
            questionText = "Tell me about yourself, your educational background, and why you are excited to begin your career with us.",
            category = "General & Fit",
            difficulty = "Core Behavioral",
            guidanceTip = "Keep it structured: 1) Who you are & your degree, 2) Key academic/internship highlights with technical skills, 3) Why this specific role aligns with your career trajectory.",
            expectedElements = listOf(
                "Degree and major / relevant coursework",
                "1-2 specific projects or internship experiences",
                "Key technical or analytical tools used",
                "Clear enthusiasm for the company's mission"
            ),
            sampleAnswerGood = "I recently graduated with a B.S. in Computer Science with a focus on mobile applications and distributed systems. During my junior and senior years, I interned at a health-tech startup where I built reactive UI components in Kotlin and Jetpack Compose, helping reduce screen loading latency by 28% for over 15,000 monthly active users. Additionally, for my capstone project, our team of four designed a collaborative campus event discovery app using Firebase and Room persistence. I'm especially drawn to this entry-level engineering role because of your team's commitment to high-impact products and strong engineering mentorship. I'm eager to contribute my mobile development foundation while learning best practices from senior engineers.",
            sampleAnswerBrief = "I graduated with a Computer Science degree. I know Kotlin and Java and did some school projects. I really like your company and want to get a job here to learn more about software."
        ),
        InterviewQuestion(
            id = "q2_technical_challenge",
            questionText = "Walk me through a difficult technical bug or project obstacle you encountered. How did you diagnose and solve it?",
            category = "Problem Solving",
            difficulty = "Entry Level",
            guidanceTip = "Use the STAR method: Describe the specific Situation, the Task at hand, the systematic Action you took to debug it, and the final measurable Result.",
            expectedElements = listOf(
                "Specific context/system description",
                "Diagnosis strategy (logs, profiling, unit tests)",
                "The proactive fix implemented",
                "Outcome, metrics, or lessons learned"
            ),
            sampleAnswerGood = "In our advanced mobile systems class, our team built a real-time collaborative whiteboard app. Right before beta testing, we noticed significant frame drops and an OutOfMemory crash whenever more than three users drew simultaneously. As the lead developer on state management, I used Android Studio Profiler and heap dump analysis to isolate the issue. I discovered that every canvas stroke was allocating immutable path objects without caching or recycling memory. I refactored the rendering engine to utilize reusable draw paths and implemented a memory-efficient buffer pool. This eliminated memory spikes entirely, restored a consistent 60 frames per second on test devices, and taught me the critical importance of memory profiling in client-side development.",
            sampleAnswerBrief = "My group project crashed because there was too much memory used. I looked at the code, found a bug in the loop, and fixed it so it didn't crash anymore."
        ),
        InterviewQuestion(
            id = "q3_team_conflict",
            questionText = "Describe a situation where you worked on a team project and had a disagreement or differing technical opinion with a teammate.",
            category = "Behavioral & Teamwork",
            difficulty = "Core Behavioral",
            guidanceTip = "Interviewers want to see high emotional intelligence, active listening, objective evaluation of ideas, and prioritizing team success over personal ego.",
            expectedElements = listOf(
                "Context of the group project",
                "The core disagreement explained respectfully",
                "How you communicated and found common ground",
                "The positive team outcome and project delivery"
            ),
            sampleAnswerGood = "During our software engineering capstone, my teammate and I disagreed on whether to use a NoSQL database or a relational PostgreSQL schema for our inventory tracker. My teammate advocated for MongoDB for rapid prototyping, while I was concerned about data integrity and ACID transaction guarantees for financial inventory counts. Rather than debating abstractly, I proposed building a quick 2-hour benchmark spike comparing both approaches against our top three user stories. After reviewing the spike together, we saw that our strict relational data model prevented duplicate transactions and required less custom validation code. My teammate agreed with the findings, and we successfully delivered the sprint on schedule, earning the department's top capstone score.",
            sampleAnswerBrief = "My partner and I argued about which database to use. We talked about it and decided to use PostgreSQL because it was better for our project."
        ),
        InterviewQuestion(
            id = "q4_fast_learning",
            questionText = "Tell me about a time you had to learn a new tool, language, or framework rapidly with minimal guidance.",
            category = "Growth & Learning",
            difficulty = "Entry Level",
            guidanceTip = "Highlight your learning methodology: documentation research, small proof-of-concept tests, asking targeted questions, and rapid application.",
            expectedElements = listOf(
                "The unfamiliar technology or framework",
                "Your self-driven learning strategy",
                "What you built or delivered with it",
                "How this prepared you for rapid workplace onboarding"
            ),
            sampleAnswerGood = "During my summer internship, our team decided to transition an internal admin dashboard to Jetpack Compose, a framework I had never used prior to that week. To get up to speed quickly, I spent the weekend completing the official Google Compose codelabs and studied the architecture of our existing codebase. On Monday, I created a self-contained prototype card component to validate state hoisting patterns with my tech lead. Within two weeks, I independently migrated four major dashboard screens, reducing codebase XML clutter by 35% and delivering two days ahead of our sprint deadline. This experience proved to me that with disciplined curiosity and official documentation, I can quickly master any modern tech stack.",
            sampleAnswerBrief = "I had to learn Compose for my internship. I watched some YouTube tutorials and read Google docs, and then I wrote the UI screens for the app."
        ),
        InterviewQuestion(
            id = "q5_competing_deadlines",
            questionText = "How do you prioritize your time when faced with multiple urgent deadlines or overlapping projects?",
            category = "Adaptability & Resilience",
            difficulty = "Core Behavioral",
            guidanceTip = "Explain your organizational framework: task prioritization (Eisenhower matrix, Jira, calendars), stakeholder communication, and focused execution.",
            expectedElements = listOf(
                "Concrete scenario with overlapping deadlines",
                "Prioritization and time management tools",
                "Proactive communication with professors or managers",
                "Successful delivery across all obligations"
            ),
            sampleAnswerGood = "Last semester, my finals week coincided with delivering the final MVP milestone for our client-sponsored senior design project and leading review sessions as a CS teaching assistant. To prevent burnout and missed deadlines, I created a prioritized daily roadmap using Notion and time-blocking techniques. I identified that the client deliverable required team synchronization, so I scheduled paired work sessions early in the week and proactively updated our faculty advisor on our milestone progress. By breaking each major goal into 90-minute focused sprint intervals, I achieved an A in all courses, held three successful TA review sessions for 60 students, and delivered our team project bug-free two days before the showcase.",
            sampleAnswerBrief = "I make a to-do list on my phone and work hard until everything gets done. I try not to procrastinate and finish early."
        ),
        InterviewQuestion(
            id = "q6_weakness_and_growth",
            questionText = "What is an area of growth or skill you have actively worked to improve over the past year?",
            category = "Growth & Learning",
            difficulty = "Core Behavioral",
            guidanceTip = "Choose an authentic skill, explain what triggered your awareness, describe proactive concrete steps taken, and show measurable improvement.",
            expectedElements = listOf(
                "Honest, professional growth area",
                "Specific steps taken (courses, practice, feedback)",
                "Measurable progress or outcome",
                "Commitment to continuous learning"
            ),
            sampleAnswerGood = "Earlier in my college career, I tended to hesitate before asking questions during team sprint planning because I worried about slowing others down. I realized this actually caused delays when assumptions went unverified. Over the past year, I actively worked to reframe this habit: whenever I encounter an ambiguous requirement, I spend 15 minutes researching independently and then bring a structured question with two proposed solutions to my mentor or teammates. This shift drastically accelerated my ramp-up time during my senior internship, where my manager commended my proactive communication and clear engineering inquiries.",
            sampleAnswerBrief = "I used to be nervous about public speaking, so I took a speech class and now I feel much better about talking in front of groups."
        )
    )

    val categories: List<String> = listOf(
        "All Categories",
        "General & Fit",
        "Behavioral & Teamwork",
        "Problem Solving",
        "Growth & Learning",
        "Adaptability & Resilience"
    )
}
