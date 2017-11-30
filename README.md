Usage:

    docker run --rm -dit --name dns-stress --env-file secrets-env.properties gesellix/dns-stress:2017-11-30T14-09-48 1 google.de

The file `secrets-env.properties` contains the `SLACK_WEBHOOK_URL` to send notifications on failed lookups.
