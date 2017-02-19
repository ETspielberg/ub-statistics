# ub-statistics

This represents personalized web application for the weeding of printed and online resources. The app is called the FachRef-Assistant. Special attention has been paid to the criteria individualization, transparency of the parameters used, library standards and generic funtions. It is based on several tools from the [mycore-framework](http://www.mycore.de) also to be found on [GitHub](https://github.com/MyCoRe-Org/mycore). The program is completely in german up to now.

## Modules
The FachRef-Assistant consists of several modules:

1. The Protokoll, allowing for a detailed analysis of a single title with respect to lending, requesting and stock.
2. The Profiles, allowing for an individual analysis of given subject areas and collections.
3. The Hitlists, allowing for an overview over the requests within a subject area.
3. The e-Journals, allowing for the computation of Journal-Usage-Metrics
4. The Stock, allowing for a overall usage analysis of different subject areas.

In addition several administrative modules exist:

1. Settings, to specify the user's subject, email etc.
2. Admin, to manage users and their roles
3. eMedia, to handle the input of usage data, prices and metrics for e-journals
4. help, a help for the individual modules.

The FachRef-Assitant allows for the inclusion of subject specific usage analysis as well as the differences in analysis between different collections within one subject area. The parameter sets used to analyze the stock and to prepare deinventarization and purchase proposal lists are stored as XML files and are included in the generated lists. These lists are similarly produced in XML format, ensuring a high degree of reproducibility and transparency for an extended period of time. To keep the application as generic as possible, all procedures specific to the local library system have been collected in one package. Hence simple adaption of this package renders the FachREf-Assistant compatible to other library systems. The inclusion of library specific properties such as collections and systematics have been designed to be highly generic.

## Requirements
Up to now, the FachRef-Assistant only works with the ExLibris Aleph system, however, other systems shall be attached in the future. The FachRef-Assistatn runs as a Java web application and just needs to be deployed as war-file. The content of the folder localdata needs to be copied to /home/ *username* /.mycore/ub-statistics/ (Linux-system) or c:\Users\ *username* \AppData\Local\MyCoRe\ub-statistics\. Additional Configurations need to be made via the mycore.proprties file, the systematics used are to be copied in the xml subfolder.



