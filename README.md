# datashare-preview
A Spring boot project to create a preview from a files attached to a dspace item.


<h3>Packaging</h3>
Create war file with

`mvn clean package`

<h3> Preparation for deployment on Datashare VM</h3>
Create *datashare-preview* in the home directory.

`mkdir datashare-preview `

SCP the war file *datashare-preview-0.0.1-SNAPSHOT.war* to *~/datashare-files*.

Copy war file to *~/datashare-preview*

`cp ~/datashare-files/datashare-preview-0.0.1-SNAPSHOT.war  ~/datashare-preview`

Within *~/datashare-preview* unzip the war file

`unzip datashare-preview-0.0.1-SNAPSHOT.war`

Then delete *datashare-preview-0.0.1-SNAPSHOT.war*

`rm datashare-preview-0.0.1-SNAPSHOT.war`

<h3>Changes to Tomcat and Apache on VM</h3>

To the Tomcat *conf/server.xml* file add line in bold

&lt;!--- DATASHARE --&gt;

&lt;Context path="/docs"  docBase="/dspace/datashare-static-docs/docs" /&gt;

&lt;Context path="/healthcheck"  docBase="/dspace/datashare-static-docs/healthcheck" /&gt;

&lt;Context path="/download"  docBase="/var/dataexchange/datasets-lac-test" /&gt;

**&lt;Context path="/datashare-preview"  docBase="/home/lib/dspace/datashare-preview" /&gt;**

&lt;/Host&gt;

&lt;/Engine&gt;

&lt;/Service&gt;

&lt;/Server&gt;


To the Apache */etc/httpd/conf/httpd.conf* add last line 

`sudo /usr/bin/rvim /etc/httpd/conf/httpd.conf`

RewriteCond %{REQUEST_URI} ^/(?!rest).*

RewriteCond %{REQUEST_URI} ^/(?!lni).*

RewriteCond %{REQUEST_URI} ^/(?!datashare-preview).* 

Then restart Tomcat 

`sudo systemctl restart tomcat`

Then restart Apache

`/usr/bin/systemctl restart httpd`
