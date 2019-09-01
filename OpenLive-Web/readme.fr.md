Ouvrez Live pour le Web
française 

Ce tutoriel vous montre comment créer rapidement une application en direct à l'aide du SDK Agora Web.
Conditions préalables
Node.js 6.9.1+
Un serveur Web prenant en charge SSL (https)
Démarrage rapide
Cette section explique comment préparer, générer et exécuter le modèle d'application.

Obtenir un App ID
Pour générer et exécuter l’exemple d’application, obtenez un App ID:

Créez un compte développeur sur agora.io. Une fois le processus d'inscription terminé, vous serez redirigé vers le tableau de bord.

Naviguez dans l’arborescence du tableau de bord à gauche pour Projects > Project List.

Sauver la App ID depuis le tableau de bord pour une utilisation ultérieure.

Générer un jeton d'accès temporaire (valable 24 hours) depuis la page du tableau de bord avec le nom de canal donné, enregistrez-le pour une utilisation ultérieure.

Ouvrez le src/utils/Settings.js file.Au bas du fichier, remplacez <#YOUR APP ID#> avec le App ID, et affectez la variable de jeton avec le jeton d’accès temporaire généré à partir du tableau de bord.

Remarque: Placer le App ID/Token entre guillemets simples ou doubles.

exportation const APP_ID_LIVE = <#YOUR APP ID#>;

// Attribuer Token à null si vous n'avez pas activé app certificate
export const Token = "<#YOUR TEMP TOKEN HERE#>";
Installer des dépendances et intégrer le SDK Agora Video
En utilisant le Terminal app,entrer le install command dans votre répertoire de projet. TCette commande installe les bibliothèques nécessaires à l’exécution de l’exemple d’application.
# installer des dépendances
npm install
Lancez l'application en entrant le run dev ou run build command. Le run dev command est à des fins de développement.
# servir avec recharge chaude à localhost:8080
npm run dev
Le run build command est à des fins de production et réduit le code.
# construit pour la production avec minification
npm run build
Votre navigateur par défaut devrait s'ouvrir et afficher l'exemple d'application. Remarque: dans certains cas, vous devrez peut-être ouvrir un navigateur et entrer http://localhost:8080 comme l'URL.
Ressources
Vous pouvez trouver le document complet sur l'API sur le Centre de documentation.
Vous pouvez déposer des bugs sur cette démo en question
Licence
The MIT License (MIT)