package pl.ciruk.security.shiro;

import com.diffplug.common.base.Errors;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.ldap.DefaultLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.apache.shiro.subject.PrincipalCollection;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toSet;

class AuthorizationLdapRealm extends DefaultLdapRealm {
    @Override
    protected AuthorizationInfo queryForAuthorizationInfo(PrincipalCollection principals, LdapContextFactory ldapContextFactory) throws NamingException {
        String username = (String) getAvailablePrincipal(principals);
        LdapContext ldapContext = ldapContextFactory.getSystemLdapContext();

        Set<String> roleNames;

        try {
            roleNames = getRoleNamesForUser(getUserDn(username), ldapContext);
        } finally {
            LdapUtils.closeContext(ldapContext);
        }

        return new SimpleAuthorizationInfo(roleNames);
    }

    protected Set<String> getRoleNamesForUser(String userDn, LdapContext ldapContext) throws NamingException {
        String searchName = "ou=groups,dc=cern,dc=ch";
        String searchFilter = "(&(objectClass=*)(member={0}))";
        Object[] searchArguments = new Object[]{userDn};

        NamingEnumeration<SearchResult> answer = ldapContext.search(
                searchName,
                searchFilter,
                searchArguments,
                new SearchControls());

        return streamOf(answer)
                .map(SearchResult::getAttributes)
                .map(Attributes::getAll)
                .flatMap(AuthorizationLdapRealm::streamOf)
                .filter(attribute -> attribute.getID().equals("ou"))
                .flatMap(Errors.rethrow().wrap(LdapUtils::getAllAttributeValues).andThen(Collection::stream))
                .collect(toSet());
    }

    static <T> Stream<T> streamOf(NamingEnumeration<? extends T> enumeration) {
        return streamOf(enumeration::hasMoreElements, enumeration::nextElement);
    }

    static <T> Stream<T> streamOf(BooleanSupplier hasNext, Supplier<T> next) {
        Iterator<T> iterator = new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return hasNext.getAsBoolean();
            }

            @Override
            public T next() {
                return (T) next.get();
            }
        };

        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(
                iterator, Spliterator.IMMUTABLE
        );
        Stream<T> stream = StreamSupport.stream(spliterator, false);

        return stream;
    }
}
