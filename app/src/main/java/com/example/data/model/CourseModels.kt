package com.example.data.model

data class TrainingModule(
    val id: String,
    val title: String,
    val pole: String, // "Courtes (1j)", "Intensives (10j)", "Spécialisée (2j)"
    val duration: String,
    val description: String,
    val objective: String,
    val proverbsFr: String,
    val proverbsLocal: String,
    val quoteLanguage: String = "Mooré",
    val contentSections: List<ContentSection> = emptyList(),
    val audioDuration: String = "4 mins",
    val audioTitleDioula: String = ""
)

data class ContentSection(
    val heading: String,
    val text: String,
    val situationExample: String = ""
)

data class GlossaryItem(
    val termFr: String,
    val termDioula: String,
    val pronunciation: String,
    val definition: String
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

object CourseData {
    val glossaryList = listOf(
        GlossaryItem(
            termFr = "Ubuntu (Philosophie)",
            termDioula = "Ubuntu / Mogobaya",
            pronunciation = "oo-BOON-too / maw-gaw-bah-yah",
            definition = "« Je suis parce que nous sommes ». Signifie que notre humanité et notre succès dépendent de la communauté."
        ),
        GlossaryItem(
            termFr = "Leadership Serviteur",
            termDioula = "Lajɛ-baga min bɛ baara kɛ bɛɛ yɛ",
            pronunciation = "lah-jeh-bah-gah meen beh bah-rah keh beh yeh",
            definition = "Mettre les besoins de son équipe en priorité absolue pour encourager leur croissance et autonomie."
        ),
        GlossaryItem(
            termFr = "Prendre une décision",
            termDioula = "Kiri tigɛ",
            pronunciation = "kee-ree tee-gheh",
            definition = "Trancher un problème avec fermeté, compassion et courage, même en situation complexe."
        ),
        GlossaryItem(
            termFr = "Vision",
            termDioula = "Ɲɛsinnukan / Latigɛlan",
            pronunciation = "gnyeh-seen-noo-kahn / lah-tee-gheh-lahn",
            definition = "La direction ou la boussole guidant un groupe vers un objectif commun."
        ),
        GlossaryItem(
            termFr = "Écoute Active",
            termDioula = "Tulo tigɛ k'a lamɛn",
            pronunciation = "too-law tee-gheh kah lah-mehn",
            definition = "Écouter avec l'intention sincère de comprendre et d'accueillir la position de l'autre."
        )
    )

    val modules = listOf(
        TrainingModule(
            id = "c1",
            title = "Leadership stratégique & Serviteur",
            pole = "Courtes (1j)",
            duration = "1 Jour",
            description = "Former des leaders-animateurs au service de l'autonomie collective.",
            objective = "Passer de la posture de chef autoritaire traditionnel au leader serviteur orienté par Ubuntu.",
            proverbsFr = "Un chef ne marche pas devant… il marche avec.",
            proverbsLocal = "Neda sên poore, n sên kông tond n beeg-a.",
            quoteLanguage = "Mooré",
            audioTitleDioula = "Mogobaya ni Lajɛ-baara",
            contentSections = listOf(
                ContentSection(
                    heading = "Du « Chef » au « Leader-Animateur Serviteur »",
                    text = "Le leadership moderne n'est pas un titre, c'est une posture de service. Le leader moderne est un serviteur qui pose des questions, partage l'information, délègue en confiance, et propulse la réussite de l'équipe.",
                    situationExample = "Exemple: À la SONABEL (Ouagadougou), Amadou coordonne un projet complexe sans autorité hiérarchique directe. Au lieu d'imposer, il lève les obstacles de l'équipe et celle-ci livre le projet en avance."
                ),
                ContentSection(
                    heading = "Les 5 Niveaux de Leadership",
                    text = "L'influence se gravit par paliers: Niveau 1 (La Position/titre) -> Niveau 2 (La permission/relation) -> Niveau 3 (La production/résultats) -> Niveau 4 (Le développement/coaching) -> Niveau 5 (Le Sommet/héritage). Le leader de niveau 4 crée d'autres leaders.",
                    situationExample = "Exemple: Moussa, entrepreneur à Koudougou, est passé du niveau 2 d'affection relationnelle au niveau 4 en formant deux adjoints de confiance."
                )
            )
        ),
        TrainingModule(
            id = "c2",
            title = "Management transversal",
            pole = "Courtes (1j)",
            duration = "1 Jour",
            description = "Piloter avec succès des projets collaboratifs sans lien hiérarchique.",
            objective = "Savoir influencer, communiquer et mobiliser des collaborateurs de divers services.",
            proverbsFr = "Seul on va plus vite, ensemble on va plus loin.",
            proverbsLocal = "I kɛlɛnna tɛ dugu sɔbɛ bila.",
            quoteLanguage = "Dioula",
            audioTitleDioula = "Gasi siri-baara bɛɛ ye",
            contentSections = listOf(
                ContentSection(
                    heading = "La Légitimité par le Service",
                    text = "Puisqu'on n'a pas de lien de subordination directe, l'influence s'acquiert par la réciprocité, l'écoute des besoins mutuels, et l'alignement des priorités de chacun.",
                    situationExample = "Exemple: Fatou rassemble un comité de 4 ministères à Ouaga pour un événement urgent en expliquant l'intérêt de chacun."
                )
            )
        ),
        TrainingModule(
            id = "c3",
            title = "L'Art du Feedback constructif",
            pole = "Courtes (1j)",
            duration = "1 Jour",
            description = "Développer la culture de l'apprentissage par le retour constructif.",
            objective = "Maîtriser la méthode DESC (Décrire, Exprimer, Spécifier, Conclure) pour recadrer positivement.",
            proverbsFr = "Ta voix compte - dis-moi la vérité avec respect.",
            proverbsLocal = "I ka kɛlɛnna min bɛɛ yɛlɛma.",
            quoteLanguage = "Dioula",
            audioTitleDioula = "Kuma juman ni ɲɛfɔli",
            contentSections = listOf(
                ContentSection(
                    heading = "La Méthode DESC",
                    text = "La critique crée le blocage; le feedback DESC construit: 1. Décrire les faits objectifs. 2. Exprimer notre ressenti ou l'impact. 3. Spécifier une solution claire. 4. Conclure d'un accord positif.",
                    situationExample = "Exemple: Recadrer doucement mais fermement un assistant en retard à Bobo, pour sauver sa réputation et sa rigueur."
                )
            )
        ),
        TrainingModule(
            id = "c4",
            title = "Intelligence relationnelle",
            pole = "Courtes (1j)",
            duration = "1 Jour",
            description = "Gérer les émotions et décoder les conflits au sein des équipes.",
            objective = "Développer son empathie et son intelligence émotionnelle au service de l'harmonie de travail.",
            proverbsFr = "La patience est un chemin de sagesse.",
            proverbsLocal = "Muɲu de bɛ koo duman da.",
            quoteLanguage = "Dioula",
            audioTitleDioula = "Hakili sɔbɛ ni mogoya",
            contentSections = listOf(
                ContentSection(
                    heading = "L'Empathie d'Ubuntu",
                    text = "L'intelligence relationnelle s'enracine dans la capacité à se mettre à la place de l'autre sans préjugé, en valorisant son appartenance et son bien-être.",
                    situationExample = "Exemple: Résoudre une jalousie de poste entre deux ingénieures agronomes à Banfora par un Cercle de parole."
                )
            )
        ),
        TrainingModule(
            id = "i1",
            title = "Soft Skills & Leadership Complet",
            pole = "Intensives (10j)",
            duration = "10 Jours",
            description = "Le parcours holistique d'excellence pour cadres et entrepreneurs burkinabè.",
            objective = "Combiner éloquence, négociation, gestion constructive du stress, efficacité pro et posture d'influence.",
            proverbsFr = "L'arbre solide tire sa force de ses racines cachées.",
            proverbsLocal = "Tiig sên zẽe ne pãnga, tɩ vãado pa sãame.",
            quoteLanguage = "Mooré",
            audioTitleDioula = "Kunu gondo donni kɛnɛ bɛɛ ye",
            contentSections = listOf(
                ContentSection(
                    heading = "La force relationnelle",
                    text = "Les 10 jours reprennent la négociation raisonnée, l'intelligence contextuelle et l'organisation du temps face aux imprévus.",
                    situationExample = "Exemple: Un digne gérant de coopérative améliore ses ventes de coton de 40% grâce à une assertivité respectueuse."
                )
            )
        ),
        TrainingModule(
            id = "i2",
            title = "Marketing Digital Localisé",
            pole = "Intensives (10j)",
            duration = "10 Jours",
            description = "Conquérir de nouveaux marchés en Afrique de l'Ouest.",
            objective = "Savoir utiliser les réseaux, les outils d'IA et WhatsApp Business pour vendre localement.",
            proverbsFr = "Si tu n'as pas de pirogue, apprivoise le crocodile.",
            proverbsLocal = "Kun di dumu dugula, kun fari te dumu.",
            quoteLanguage = "Dioula",
            audioTitleDioula = "Dugumakolo baara ni kɛnɛbaara",
            contentSections = listOf(
                ContentSection(
                    heading = "WhatsApp Business et l'IA",
                    text = "Comment transformer les interactions en ligne en ventes réelles en s'adaptant à la réalité des forfaits d'économie de données (data limités).",
                    situationExample = "Exemple: Une marque de cosmétique naturelle à Ouahigouya utilise WhatsApp de façon frugale et efficace."
                )
            )
        ),
        TrainingModule(
            id = "i3",
            title = "Orientation Entrepreneuriat",
            pole = "Intensives (10j)",
            duration = "10 Jours",
            description = "De l'idée au projet structuré et finançable.",
            objective = "Bâtir des modèles économiques résilients, rédiger un business plan et convaincre des investisseurs locaux.",
            proverbsFr = "Celui qui sème le maïs récolte le maïs.",
            proverbsLocal = "Sên yi bumb, yaa sên tũ bumb.",
            quoteLanguage = "Mooré",
            audioTitleDioula = "Baara kelen togo daka",
            contentSections = listOf(
                ContentSection(
                    heading = "Modèle Économique Résilient",
                    text = "Les pivots nécessaires face aux crises de la chaîne d'approvisionnement et aux interruptions de connectivité.",
                    situationExample = "Exemple: Pivot d'un transformateur de céréales en emballages locaux en carton compressé."
                )
            )
        ),
        TrainingModule(
            id = "s1",
            title = "L'IA utile au quotidien",
            pole = "Spécialisée (2j)",
            duration = "2 Jours",
            description = "Démystifier et adopter l'IA générative dans son travail de bureau.",
            objective = "Multiplier par 3 sa productivité administrative avec des invites (prompts) efficaces.",
            proverbsFr = "L'outil ne fait pas l'artisan, mais il dédouble sa force.",
            proverbsLocal = "Sɛbɛ kɔ bɛɛ tɛ tògoli ye.",
            quoteLanguage = "Dioula",
            audioTitleDioula = "Hakili fɔlan ni baara bɛɛ kono",
            contentSections = listOf(
                ContentSection(
                    heading = "Prompter de façon structurée",
                    text = "Maîtriser les rôles, les contextes et les livrables souhaités de l'IA pour générer plans, rapports ou courriels de haute volée en quelques secondes.",
                    situationExample = "Exemple: Une assistante de direction à la Chambre de Commerce rédige un compte rendu de 40 pages en 5 minutes."
                )
            )
        )
    )

    val quizList = mapOf(
        "c1" to listOf(
            QuizQuestion(
                id = 1,
                question = "Quelle philosophie africaine est la base du Leadership Serviteur modernisé ?",
                options = listOf("Le repli tactique", "Sankofa", "Ubuntu (« Je suis parce que nous sommes »)", "L'isolement hiérarchique"),
                correctIndex = 2,
                explanation = "Ubuntu place la communauté et le service aux autres au cœur de la force du leader."
            ),
            QuizQuestion(
                id = 2,
                question = "À quel niveau du leadership selon Maxwell crée-t-on le développement de l'autonomie chez les collaborateurs (mentorat) ?",
                options = listOf("Niveau 1 (La Position)", "Niveau 2 (La Relation)", "Niveau 4 (Le Développement des Personnes)", "Niveau 5 (Le Sommet)"),
                correctIndex = 2,
                explanation = "Le Niveau 4 est précisément celui du développement humain et du coaching pour l'autonomie du collaborateur."
            )
        ),
        "c2" to listOf(
            QuizQuestion(
                id = 1,
                question = "Dans le management transversal, comment gagne-t-on la légitimité ?",
                options = listOf("Par la coercition", "Par l'écoute active et le service réciproque", "Par des ordres stricts", "En ignorant les objections"),
                correctIndex = 1,
                explanation = "Parce que vous n'avez pas d'autorité hiérarchique, vous devez susciter l'adhésion par le service mutuel."
            )
        ),
        "c3" to listOf(
            QuizQuestion(
                id = 1,
                question = "Qu'indique l'acronyme DESC en feedback ?",
                options = listOf("Dessiner, Entrer, Sortir, Couper", "Dire, Écrire, S'organiser, Changer", "Décrire, Exprimer, Spécifier, Conclure", "Diluer, Enlever, Saboter, Cacher"),
                correctIndex = 2,
                explanation = "Décrire les faits, Exprimer l'impact, Spécifier des solutions, Conclure par un accord positif."
            )
        ),
        "c4" to listOf(
            QuizQuestion(
                id = 1,
                question = "Où s'enracine principalement l'intelligence relationnelle ?",
                options = listOf("L'empathie, la patience et l'écoute active", "L'autorité formelle", "L'évitement des conversations", "La supériorité technique"),
                correctIndex = 0,
                explanation = "Développer son empathie et son écoute active est la base pour apaiser les tensions et fédérer."
            )
        )
    )
}
