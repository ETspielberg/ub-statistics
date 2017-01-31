<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Ausgabe eines MABXML Datensatzes im ISBD-Format -->
<!-- Autor: Volker Lenhardt -->

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
  exclude-result-prefixes="xsl mabxml"
>

  <xsl:template match="mabxml:datensatz">

    <!-- 
      <xsl:for-each select="mabxml:feld[@nr='010']/mabxml:uf[@code='a']">
        <xsl:apply-templates select="document(concat('mabxml:',text()))/mabxml:datensatz" mode="details" />
      </xsl:for-each>
    -->

    <p>
      <xsl:choose>
        <xsl:when test="@typ='h'">
          <xsl:call-template name="kopf" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="mabxml:feld[@nr='089']" />
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="mabxml:feld[(@nr &gt;='310' and @nr &lt;='425') and @nr !='370']" />

      <xsl:if test="boolean(mabxml:feld[@nr='610'])=false">
        <xsl:apply-templates select="mabxml:feld[@nr='652']" />
      </xsl:if>
      <xsl:apply-templates select="mabxml:feld[@nr='653']" />
      <xsl:apply-templates select="mabxml:feld[@nr='433']" />
      <xsl:apply-templates select="mabxml:feld[@nr='437']" />
      <xsl:apply-templates select="mabxml:feld[@nr='461']" />
      <xsl:apply-templates select="mabxml:feld[@nr='471']" />
      <xsl:apply-templates select="mabxml:feld[@nr='481']" />
      <xsl:apply-templates select="mabxml:feld[@nr='491']" />
      <!-- 
      <xsl:apply-templates select="mabxml:feld[@nr='651' or @nr='654']" />
      <xsl:apply-templates select="mabxml:feld[@nr='655']" />
      <xsl:apply-templates select="mabxml:feld[@nr &gt;='501' and @nr &lt;='524']" />
      <xsl:apply-templates select="mabxml:feld[@nr &gt;='610' and @nr &lt;='619']" />
      <xsl:if test="mabxml:feld[@nr='610']">
        <xsl:apply-templates select="mabxml:feld[@nr='652']" />
      </xsl:if>
      <xsl:apply-templates select="mabxml:feld[@nr='637']" />
      <xsl:apply-templates select="mabxml:feld[@nr &gt;='624' and @nr &lt;='636']" />
      <xsl:apply-templates select="mabxml:feld[@nr &gt;='525' and @nr &lt;='534']" />
      <xsl:apply-templates select="mabxml:feld[@nr &gt;='540' and @nr &lt;='543' and @ind !='z']" />
 -->
    </p>
  </xsl:template>

  <!-- Setzt einen Punkt, wenn der vorhergehende Knoten nicht mit Punkt endet. Übernimmt optional die Anzahl zu übergehender Knoten. -->
  <xsl:template name="endpunkt">
    <xsl:param name="abstand" select="0" />
    <xsl:variable name="akttext" select="preceding::*[1 + $abstand]" />
    <xsl:if test="substring($akttext,string-length($akttext)) != '.'">
      <xsl:text>.</xsl:text>
    </xsl:if>
  </xsl:template>

  <!-- Kopfzeile, falls Verfasser- oder Urheberwerk -->
  <xsl:template name="kopf">
    <xsl:choose>
      <xsl:when test="mabxml:feld[@nr='100' and @ind=' ']">
        <b>
          <xsl:apply-templates  select="mabxml:feld[@nr='100']/mabxml:uf[@code='a']|mabxml:feld[@nr='100']/mabxml:uf[@code='p']" />
          <xsl:text>:</xsl:text>
        </b>
        <br />
      </xsl:when>
      <xsl:when test="mabxml:feld[@nr='200' and @ind=' ']">
        <b>
          <xsl:apply-templates select="mabxml:feld[@nr='200']/mabxml:uf[@code='a']|mabxml:feld[@nr='200']/mabxml:uf[@code='p']" />
          <xsl:text>:</xsl:text>
        </b>
        <br />
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- Setzt nichtsortierende mabxml:uf-Inhalte kursiv -->
  <xsl:template match="mabxml:ns">
    <i>
      <xsl:value-of select="text()" />
    </i>
  </xsl:template>

  <!-- Sammlungsvermerk -->
  <xsl:template match="mabxml:feld[@nr='300']">
    <xsl:text>[</xsl:text>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
    <xsl:text>]</xsl:text>
  </xsl:template>

  <!-- Personennameneintraege -->
  <xsl:template match="mabxml:feld[@nr &gt;='100' and @nr &lt;='196']">
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
    <xsl:apply-templates select="mabxml:uf[@code='p']" />
  </xsl:template>


  <!-- Ansetzungssachtitel -->
  <xsl:template match="mabxml:feld[@nr='310']">
    <xsl:choose>
      <xsl:when test="(../mabxml:feld[@nr='100' and @ind=' ']) or (../mabxml:feld[@nr='200' and @ind=' '])">
        <xsl:text>[</xsl:text>
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
        <xsl:text>] </xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <b>
          <xsl:text>[</xsl:text>
          <xsl:apply-templates select="mabxml:uf[@code='a']" />
          <xsl:text>] </xsl:text>
        </b>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Hauptsachtitel -->
  <xsl:template match="mabxml:feld[@nr='331']">
    <xsl:choose>
      <xsl:when test="../@typ='h'">
        <xsl:choose>
          <xsl:when test="(../mabxml:feld[@nr='100' and @ind=' ']) or (../mabxml:feld[@nr='200' and @ind=' ']) or (../mabxml:feld[@nr='310'])">
            <xsl:apply-templates select="mabxml:uf[@code='a']" />
          </xsl:when>
          <xsl:otherwise>
            <b>
              <xsl:apply-templates select="mabxml:uf[@code='a']" />
            </b>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="../@typ='u'">
        <xsl:call-template name="endpunkt" />
        <xsl:text> </xsl:text>
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- Zu ergaenzender Urheber, bzw. Verfasserangabe -->
  <xsl:template match="mabxml:feld[@nr='333' or @nr='342' or @nr='346' or @nr='359']">
    <xsl:choose>
      <xsl:when test="@nr='359' and ../@typ='u' and boolean(../mabxml:feld[@nr='331'])=false() and boolean(../mabxml:feld[@nr='335'])=false()">
        <xsl:call-template name="endpunkt" />
        <xsl:text> </xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text> / </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>


  <!-- Zusatz zum Sachtitel -->
  <xsl:template match="mabxml:feld[@nr='335' or @nr='343' or @nr='347' or @nr='412' or @nr='417' or @nr='434']">
    <xsl:text> : </xsl:text>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>

  <!-- Parallelsachtitel in Ansetzungsform -->
  <xsl:template match="mabxml:feld[@nr='340' or @nr='344']">
    <xsl:text> = [</xsl:text>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
    <xsl:text>]</xsl:text>
  </xsl:template>

  <!-- Parallelsachtitel in Vorlageform -->
  <xsl:template match="mabxml:feld[@nr='341' or @nr='345']">
    <xsl:if test="boolean(../mabxml:feld[@nr=current()/@nr - 1])=false">
      <xsl:text> =</xsl:text>
    </xsl:if>
    <xsl:text> </xsl:text>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>

  <!-- Unterreihe, beigefuegte Werke, Zusaetze zur gesamten Vorlage, Verfasserangabe zur gesamten Vorlage (werden mit ". " angeschlossen) -->
  <xsl:template match="mabxml:feld[@nr &gt;='360' and @nr &lt;='369']">
    <xsl:call-template name="endpunkt" />
    <xsl:text> </xsl:text>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>

  <!-- Ausgabebezeichnung -->
  <xsl:template match="mabxml:feld[@nr='403']">
    <xsl:variable name="anzahlknoten" select="count(../mabxml:feld[@nr='370']) + count(../mabxml:feld[@nr='370']/mabxml:uf) + count(../mabxml:feld[@nr='370']/mabxml:uf/ns)" />
    <xsl:call-template name="endpunkt">
      <xsl:with-param name="abstand" select="$anzahlknoten" />
    </xsl:call-template>
    <xsl:text> - </xsl:text>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>

  <!-- Erscheinungsverlauf -->
  <xsl:template match="mabxml:feld[@nr='405']">
    <xsl:choose>
      <xsl:when test="../mabxml:feld[@nr='403']">
        <xsl:call-template name="endpunkt" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="anzahlknoten" select="count(../mabxml:feld[@nr='370']) + count(../mabxml:feld[@nr='370']/mabxml:uf) + count(../mabxml:feld[@nr='370']/mabxml:uf/ns)" />
        <xsl:call-template name="endpunkt">
          <xsl:with-param name="abstand" select="$anzahlknoten" />
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text> - </xsl:text>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>

  <!-- mathematische Angaben -->
  <xsl:template match="mabxml:feld[@nr='407']">
    <xsl:choose>
      <xsl:when test="../mabxml:feld[@nr='403'] or ../mabxml:feld[@nr='405']">
        <xsl:call-template name="endpunkt" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="anzahlknoten" select="count(../mabxml:feld[@nr='370']) + count(../mabxml:feld[@nr='370']/mabxml:uf) + count(../mabxml:feld[@nr='370']/mabxml:uf/ns)" />
        <xsl:call-template name="endpunkt">
          <xsl:with-param name="abstand" select="$anzahlknoten" />
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text> - </xsl:text>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>

  
  <!-- Erscheinungsjahr -->
  <xsl:template match="mabxml:feld[@nr='425']">
    <xsl:choose>
      <xsl:when test="@ind=' '">
        <xsl:choose>
          <xsl:when test="../mabxml:feld[@nr='410']">
            <xsl:text>, </xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="../@typ='u'">
                <xsl:call-template name="endpunkt" />
                <xsl:text> - </xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <br />
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="boolean(../mabxml:feld[@nr='425' and @ind=' '])=false">
          <xsl:if test="@ind='a'">
            <xsl:choose>
              <xsl:when test="../mabxml:feld[@nr='410']">
                <xsl:text>, </xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:choose>
                  <xsl:when test="../@typ='u'">
                    <xsl:call-template name="endpunkt" />
                    <xsl:text> - </xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <br />
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="mabxml:uf[@code='a']" />
          </xsl:if>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
    Setzt das Deskriptionszeichen vor der Umfangsangabe mit Prüfung
    von nicht auszugebenden Inhalten in mabxml:feld 425 zur Vermeidung
    doppelter Punkte. (für 433 und 437) 
  -->

  <xsl:template name="deskriptzeichenvorumfang">
    <xsl:choose>
      <xsl:when test="preceding-sibling::*[1]/@nr='425' and (preceding-sibling::*[1]/@ind='b' or preceding-sibling::*[1]/@ind='c')">
        <xsl:variable name="anzahlknoten" select="count(../mabxml:feld[@nr='425']) + count(../mabxml:feld[@nr='425']/mabxml:uf)" />
        <xsl:call-template name="endpunkt">
          <xsl:with-param name="abstand" select="$anzahlknoten" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="endpunkt" />
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text> - </xsl:text>
  </xsl:template>

 
 
  <!-- Standardnummern -->
  <xsl:template match="mabxml:feld[@nr &gt;='540' and @nr &lt;='543']">
    <xsl:choose>
      <xsl:when test="position()=1">
        <br />
        <xsl:choose>
          <xsl:when test="@nr='540'">
            <xsl:text>ISBN </xsl:text>
          </xsl:when>
          <xsl:when test="@nr='541'">
            <xsl:text>ISMN </xsl:text>
          </xsl:when>
          <xsl:when test="@nr='542'">
            <xsl:text>ISSN </xsl:text>
          </xsl:when>
          <xsl:when test="@nr='543'">
            <xsl:text>ISRN </xsl:text>
          </xsl:when>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text> - </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>

  <!-- Persistent identifier -->
  <xsl:template match="mabxml:feld[@nr='552']">
    <br />
    <xsl:choose>
      <xsl:when test="@ind='a'">
        <xsl:text>DOI </xsl:text>
        <a href="concat('http://dx.doi.org/', ./mabxml:uf)">
          <xsl:apply-templates select="mabxml:uf[@code='a']" />
        </a>
      </xsl:when>
      <xsl:when test="@ind='b'">
        <xsl:text>URN </xsl:text>
        <a href="concat('http://nbn-resolving.de/urn/resolver.pl?urn=', ./mabxml:uf)">
          <xsl:apply-templates select="mabxml:uf[@code='a']" />
        </a>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

 
  <!-- Elektronische Ressource - Online-Ressource -->
  <xsl:template match="mabxml:feld[@nr='652']">
    <xsl:if test="mabxml:uf[@code='a']='Online-Ressource'">
      <xsl:choose>
        <xsl:when test="../mabxml:feld[@nr='610']">
          <xsl:if test="preceding-sibling::*[1]/@nr!='610'">
            <xsl:text>. </xsl:text>
          </xsl:if>
          <xsl:text />
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="anzahlknoten"
            select="count(../mabxml:feld[@nr &gt; '425' and @nr &lt; '652']) +
                    count(../mabxml:feld[@nr &gt; '425' and @nr &lt; '652']/mabxml:uf) +
                    count(../mabxml:feld[@nr &gt; '425' and @nr &lt; '652']/mabxml:uf/ns)" />

          <xsl:choose>
            <xsl:when test="../mabxml:feld[@nr='425']/@ind='b' or ../mabxml:feld[@nr='425']/@ind='c'">
              <xsl:variable name="anzahl425" select="count(../mabxml:feld[@nr='425']) + count(../mabxml:feld[@nr='425']/mabxml:uf)" />
              <xsl:call-template name="endpunkt">
                <xsl:with-param name="abstand" select="$anzahlknoten + $anzahl425" />
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="endpunkt">
                <xsl:with-param name="abstand" select="$anzahlknoten" />
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:text> - </xsl:text>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="mabxml:uf[@code='a']" />
    </xsl:if>
  </xsl:template>

  <!-- Elektronische Ressource - Zugang und Adresse im Fernzugriff -->
  <xsl:template match="mabxml:feld[@nr='655']">
    <xsl:choose>
      <xsl:when test="../mabxml:feld[@nr='651']">
        <xsl:call-template name="endpunkt" />
        <xsl:text> - </xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="../@typ='u'">
            <xsl:variable name="anzahlknoten"
              select="count(../mabxml:feld[@nr &gt; '499' and @nr &lt; '655']) +
                      count(../mabxml:feld[@nr &gt; '499' and @nr &lt; '655']/mabxml:uf) +
                      count(../mabxml:feld[@nr &gt; '499' and @nr &lt; '655']/mabxml:uf/ns)" />
            <xsl:call-template name="endpunkt">
              <xsl:with-param name="abstand" select="$anzahlknoten" />
            </xsl:call-template>
            <xsl:text> - </xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <br />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>Zugriffsart: </xsl:text>

    <xsl:choose>
      <xsl:when test="@ind='a'">
        <xsl:text>E-Mail</xsl:text>
      </xsl:when>
      <xsl:when test="@ind='b'">
        <xsl:text>FTP</xsl:text>
      </xsl:when>
      <xsl:when test="@ind='c'">
        <xsl:text>Remote Login</xsl:text>
      </xsl:when>
      <xsl:when test="@ind='d'">
        <xsl:text>Dial-Up</xsl:text>
      </xsl:when>
      <xsl:when test="@ind='e'">
        <xsl:text>HTTP</xsl:text>
      </xsl:when>
      <xsl:when test="@ind='h'">
        <xsl:value-of select="mabxml:uf[@code='2']" />
      </xsl:when>
    </xsl:choose>
    <xsl:text>. - Adresse: </xsl:text>
    <a href="{mabxml:uf[@code='u']}">
      <xsl:apply-templates select="mabxml:uf[@code='u']" />
    </a>
  </xsl:template>

  <!-- Bandangabe -->
  <xsl:template match="mabxml:feld[@nr='089']">
    <br />
    <xsl:for-each select="mabxml:uf">
      <xsl:if test="position() !=1"><xsl:text>, </xsl:text></xsl:if>
      <xsl:apply-templates select="." />
    </xsl:for-each>
  </xsl:template>
  
  <xsl:template match="mabxml:uf[@code='a']">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template match="mabxml:uf[@code='b']">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template match="mabxml:uf[@code='c']">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template match="mabxml:uf[@code='d']">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template match="mabxml:uf[@code='e']">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template match="mabxml:uf[@code='p']">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template match="mabxml:uf[@code='u']">
    <xsl:value-of select="."/>
  </xsl:template>

</xsl:stylesheet>
