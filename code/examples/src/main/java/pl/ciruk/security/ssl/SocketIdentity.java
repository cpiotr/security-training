package pl.ciruk.security.ssl;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.net.Socket;
import java.security.Principal;
import java.util.Optional;

public class SocketIdentity {
    private Optional<String> commonName;

    public SocketIdentity(Socket socket) {
        commonName = getIdentityFrom(socket);
    }

    public Optional<String> getCommonName() {
        return commonName;
    }

    public String getCommonNameOrDefault(String defaultValue) {
        return getCommonName().orElse(defaultValue);
    }

    private Optional<String> getIdentityFrom(Socket socket) {
        if (socket instanceof SSLSocket) {
            SSLSession session = ((SSLSocket) socket).getSession();
            try {
                Principal principal = session.getPeerPrincipal();
                return getCommonName(principal);
            } catch (SSLPeerUnverifiedException | InvalidNameException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }


    private Optional<String> getCommonName(Principal subject) throws InvalidNameException {
        LdapName name = new LdapName(subject.getName());
        return name.getRdns().stream()
                .filter(rdn -> rdn.getType().equalsIgnoreCase("CN"))
                .map(Rdn::getValue)
                .map(String::valueOf)
                .findAny();
    }
}
