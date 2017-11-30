Usage:

    docker run --rm -dit --name dns-stress --env-file secrets-env.properties gesellix/dns-stress:2017-11-30T22-16-31 1 google.de

The file `secrets-env.properties` contains the `SLACK_WEBHOOK_URL` to send notifications on failed lookups.

The following JVM network settings will be logged before performing dns lookups:

    networkaddress.cache.ttl
    networkaddress.cache.negative.ttl 

To get a better picture about dns related errors we also disable the negative lookup cache:

    Security.setProperty("networkaddress.cache.negative.ttl", "0");
