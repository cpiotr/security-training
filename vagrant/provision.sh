config_organization_name=Cern
config_fqdn=cern.ch
config_domain=cern.ch
config_domain_dc="dc=cern,dc=ch"
config_admin_dn="cn=admin,$config_domain_dc"
config_admin_password=admin

echo "127.0.0.1 $config_fqdn" >>/etc/hosts

debconf-set-selections <<EOF
slapd slapd/password1 password $config_admin_password
slapd slapd/password2 password $config_admin_password
slapd slapd/domain string $config_domain
slapd shared/organization string $config_organization_name
EOF

apt-get install -y --no-install-recommends slapd ldap-utils

ldapadd -D "$config_admin_dn" -w $config_admin_password -f /vagrant/sample.ldif
