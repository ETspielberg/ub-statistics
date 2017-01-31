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
      <xsl:apply-templates select="mabxml:feld[@nr='451']" />
      <xsl:apply-templates select="mabxml:feld[@nr='461']" />
      <xsl:apply-templates select="mabxml:feld[@nr='471']" />
      <xsl:apply-templates select="mabxml:feld[@nr='481']" />
      <xsl:apply-templates select="mabxml:feld[@nr='491']" />
      <xsl:apply-templates select="mabxml:feld[@nr='304']" />
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
      <xsl:apply-templates select="mabxml:feld[@nr &gt;='551' and @nr &lt;='580']" />

      <xsl:if test="@typ='h'">
        <xsl:call-template name="nes" />
      </xsl:if>
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

  <!-- Einheitssachtitel -->
  <xsl:template match="mabxml:feld[@nr='304']">
    <br />
    <xsl:text>Einheitssacht.: </xsl:text>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
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

  <!-- Allgemeine Materialbenennung -->
  <xsl:template match="mabxml:feld[@nr='334']">
    <xsl:text> [</xsl:text>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
    <xsl:text>]</xsl:text>
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

  <!-- Verlagsort -->
  <xsl:template match="mabxml:feld[@nr='410']">
    <br />
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>

  <!-- Ort des 2. Verlags bzw. Beigabenvermerk (werden mit " ; " angeschlossen) -->
  <xsl:template match="mabxml:feld[@nr='415' or @nr='435']">
    <xsl:text> ; </xsl:text>
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

  <!-- Umfangsangabe -->
  <xsl:template match="mabxml:feld[@nr='433']">
    <xsl:call-template name="deskriptzeichenvorumfang" />
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>

  <!-- Begleitmaterial -->
  <xsl:template match="mabxml:feld[@nr='437']">
    <xsl:choose>
      <xsl:when test="../mabxml:feld[@nr='433']">
        <xsl:text> + </xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="deskriptzeichenvorumfang" />
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>

  <!-- Gesamttitel -->
  <xsl:template match="mabxml:feld[@nr='451' or @nr='461' or @nr='471' or @nr='481' or @nr='491']">
    <xsl:choose>
      <xsl:when test="@nr='451'">
        <xsl:choose>
          <xsl:when test="../@typ='h'">
            <br />
            <xsl:text>(</xsl:text>
          </xsl:when>
          <xsl:when test="../@typ='u'">
            <xsl:call-template name="endpunkt" />
            <xsl:text> - (</xsl:text>
          </xsl:when>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text> (</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:choose>
      <xsl:when test="../mabxml:feld[@nr=current()/@nr + 2]">
        <a href="{../mabxml:feld[@nr=current()/@nr + 2]/mabxml:uf[@code='a']/text()}.html">
          <xsl:apply-templates select="mabxml:uf[@code='a']" />
        </a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>)</xsl:text>
  </xsl:template>

  <!-- Fussnoten -->
  <xsl:template match="mabxml:feld[@nr &gt;='501'and @nr &lt;='534']">
    <xsl:choose>
      <xsl:when test="../@typ='h' or position()=1">
        <br />
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="endpunkt" />
        <xsl:text> - </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="mabxml:uf[@code='p']">
      <xsl:apply-templates select="mabxml:uf[@code='p']" />
      <xsl:text>: </xsl:text>
    </xsl:if>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
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

  <!-- Bestellnummern -->
  <xsl:template match="mabxml:feld[@nr='551']">
    <xsl:choose>
      <xsl:when test="position()=1">
        <br />
      </xsl:when>
      <xsl:otherwise>
        <xsl:text> - </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>Best.-Nr. </xsl:text>
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

  <!-- Reportnummern -->
  <xsl:template match="mabxml:feld[@nr='556']">
    <br />
    <xsl:choose>
      <xsl:when test="@ind='a'">
        <xsl:text>Reportnr. </xsl:text>
      </xsl:when>
      <xsl:when test="@ind='b'">
        <xsl:text>Kontraktnr. </xsl:text>
      </xsl:when>
      <xsl:when test="@ind='c'">
        <xsl:text>Task-Nr. </xsl:text>
      </xsl:when>
    </xsl:choose>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>

  <!-- Sonstige Nummern -->
  <xsl:template match="mabxml:feld[@nr='580']">
    <xsl:choose>
      <xsl:when test="position()=1">
        <br />
      </xsl:when>
      <xsl:otherwise>
        <xsl:text> - </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>

  <!-- Sekundaerformen -->
  <xsl:template match="mabxml:feld[@nr &gt;='610' and @nr &lt;='647']">
    <xsl:choose>
      <xsl:when test="@nr='610'">
        <br />
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
        <xsl:text>: </xsl:text>
      </xsl:when>
      <xsl:when test="@nr='611'">
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
      </xsl:when>
      <xsl:when test="@nr='613'">
        <xsl:if test="preceding-sibling::*[1]/@nr='611'">
          <xsl:text> : </xsl:text>
        </xsl:if>
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
      </xsl:when>
      <xsl:when test="@nr='614'">
        <xsl:text> ; </xsl:text>
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
      </xsl:when>
      <xsl:when test="@nr='616'">
        <xsl:if test="preceding-sibling::*[1]/@nr='614'">
          <xsl:text> : </xsl:text>
        </xsl:if>
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
      </xsl:when>
      <xsl:when test="@nr='619'">
        <xsl:if test="preceding-sibling::*[1]/@nr!='610'">
          <xsl:text>, </xsl:text>
        </xsl:if>
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
      </xsl:when>
      <xsl:when test="@nr='621'">
        <xsl:text>. (</xsl:text>
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
        <xsl:text>)</xsl:text>
      </xsl:when>

      <xsl:when test="@nr='634'">
        <xsl:choose>
          <xsl:when test="position()=1">
            <xsl:text>. </xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text> - </xsl:text>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text>ISBN </xsl:text>
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
      </xsl:when>

      <xsl:when test="@nr='636'">
        <xsl:text>. </xsl:text>
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
      </xsl:when>

      <xsl:when test="@nr='637'">
        <xsl:text>. (</xsl:text>
        <xsl:apply-templates select="mabxml:uf[@code='a']" />
        <xsl:if test="../mabxml:feld[@nr='638']">
          <xsl:text> + </xsl:text>
          <xsl:value-of select="../mabxml:feld[@nr='638']/mabxml:uf" />
        </xsl:if>
        <xsl:text>)</xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- Elektronische Ressource - Systemvoraussetzungen -->
  <xsl:template match="mabxml:feld[@nr='651']">
    <xsl:choose>
      <xsl:when test="position()=1">
        <xsl:choose>
          <xsl:when test="../@typ='h'">
            <br />
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="anzahlknoten"
              select="count(../mabxml:feld[@nr &gt; '499' and @nr &lt; '651']) + 
                      count(../mabxml:feld[@nr &gt; '499' and @nr &lt; '651']/mabxml:uf) +
                      count(../mabxml:feld[@nr &gt; '499' and @nr &lt; '651']/mabxml:uf/ns)" />
            <xsl:call-template name="endpunkt">
              <xsl:with-param name="abstand" select="$anzahlknoten" />
            </xsl:call-template>
            <xsl:text> - </xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>

      <xsl:otherwise>
        <xsl:call-template name="endpunkt" />
        <xsl:text> - </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
    <xsl:text>: </xsl:text>
    <xsl:apply-templates select="mabxml:uf[@code='b']" />
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

  <!-- Elektronische Ressource - Physische Beschreibung -->
  <xsl:template match="mabxml:feld[@nr='653']">
    <xsl:if test="../mabxml:feld[@nr='652']/mabxml:uf[@code='a'] != 'Online-Ressource'">
      <xsl:variable name="anzahlknoten"
        select="count(../mabxml:feld[@nr &gt; '425' and @nr &lt; '653']) +
                count(../mabxml:feld[@nr &gt; '425' and @nr &lt; '653']/mabxml:uf) +
                count(../mabxml:feld[@nr &gt; '425' and @nr &lt; '653']/mabxml:uf/ns)" />
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
      <xsl:apply-templates select="mabxml:uf[@code='a']" />

      <xsl:if test="mabxml:uf[@code='b']">
        <xsl:text> (</xsl:text>
        <xsl:apply-templates select="mabxml:uf[@code='b']" />
        <xsl:text>)</xsl:text>
      </xsl:if>

      <xsl:if test="mabxml:uf[@code='c']">
        <xsl:if test="mabxml:uf[@code='a'] or mabxml:uf[@code='b']">
          <xsl:text> : </xsl:text>
        </xsl:if>
        <xsl:apply-templates select="mabxml:uf[@code='c']" />
      </xsl:if>

      <xsl:if test="mabxml:uf[@code='d']">
        <xsl:if test="mabxml:uf[@code='a'] or mabxml:uf[@code='b'] or mabxml:uf[@code='c']">
          <xsl:text> ; </xsl:text>
        </xsl:if>
        <xsl:apply-templates select="mabxml:uf[@code='d']" />
      </xsl:if>

      <xsl:if test="mabxml:uf[@code='e']">
        <xsl:if test="mabxml:uf[@code='a'] or mabxml:uf[@code='b'] or mabxml:uf[@code='c'] or mabxml:uf[@code='d']">
          <xsl:text> + </xsl:text>
        </xsl:if>
        <xsl:apply-templates select="mabxml:uf[@code='e']" />
      </xsl:if>
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

  <!-- Zweiteilige Nebeneintragungen -->
  <xsl:template match="mabxml:feld[@nr &gt;='800']">
    <xsl:apply-templates select="mabxml:uf[@code='a']" />
  </xsl:template>

  <!-- Nebeintragungsvermerk  -->
  <xsl:template name="nes">
    <xsl:for-each
      select="mabxml:feld[@nr='300']|
              mabxml:feld[@nr='304' and @ind='a']|
              mabxml:feld[@nr='310' and @ind='a']|
              mabxml:feld[@nr='331' and @ind !=' ']|
              mabxml:feld[@nr='335' and @ind='a']|
              mabxml:feld[@nr='340' and @ind='a']|
              mabxml:feld[@nr='341' and @ind='a']|
              mabxml:feld[@nr='344' and @ind='a']|
              mabxml:feld[@nr='345' and @ind='a']|
              mabxml:feld[@nr='370']|
              mabxml:feld[@nr='100' and @ind !=' ']|
              mabxml:feld[@nr &gt;='104' and @nr &lt;='196']|
              mabxml:feld[@nr='200' and @ind !=' ']|
              mabxml:feld[@nr &gt;='204' and @nr &lt;='296']|
              mabxml:feld[@nr &gt;='800' and @nr &lt;='829']">
      <xsl:choose>
        <xsl:when test="position()=1">
          <br />
          <xsl:text>NE: </xsl:text>
        </xsl:when>
        <xsl:when test="(@nr &gt;=805) and (@nr &lt;=829) and (@nr mod 6 = 1)">
          <xsl:text>: </xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>; </xsl:text>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
        <xsl:when test="@nr='300'">
          <xsl:apply-templates
            select="../mabxml:feld[@nr='100']/mabxml:uf[@code='a']" />
          <xsl:text>: </xsl:text>
          <xsl:apply-templates select="." />
        </xsl:when>
        <xsl:when test="@nr='310'">
          <xsl:text>AST</xsl:text>
        </xsl:when>
        <xsl:when test="@nr='331' and @ind='a'">
          <xsl:text>HST</xsl:text>
        </xsl:when>
        <xsl:when test="@nr='331' and @ind='b'">
          <xsl:apply-templates
            select="../mabxml:feld[@nr='100']/mabxml:uf[@code='a']" />
          <xsl:text>: HST</xsl:text>
        </xsl:when>
        <xsl:when test="@nr='340' or @nr='341'">
          <xsl:text>1. PT</xsl:text>
        </xsl:when>
        <xsl:when test="@nr='344' or @nr='345'">
          <xsl:text>2. PT</xsl:text>
        </xsl:when>
        <xsl:when test="@nr='370' or @nr='335'">
          <xsl:apply-templates select="mabxml:uf[@code='a']" />
        </xsl:when>
        <xsl:when test="@nr='304'">
          <xsl:text>EST</xsl:text>
          <xsl:if test="substring(mabxml:uf, string-length(mabxml:uf))='&gt;'">
            <xsl:text> &lt;</xsl:text>
            <xsl:value-of select="substring-after(mabxml:uf,'&lt;')" />
          </xsl:if>
        </xsl:when>
        <xsl:when test="@nr &gt;='100' and @nr &lt;='196'">
          <xsl:apply-templates select="mabxml:uf[@code='a']" />
          <xsl:if test="@ind='a'"><xsl:text>:</xsl:text></xsl:if>
          <xsl:if test="@ind='f'"><xsl:text>: Festschrift</xsl:text></xsl:if>
          <xsl:if test="mabxml:uf[@code='b']">
            <xsl:text> </xsl:text><xsl:value-of select="./mabxml:uf[@code='b']" />
          </xsl:if>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="mabxml:uf[@code='a']" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
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
