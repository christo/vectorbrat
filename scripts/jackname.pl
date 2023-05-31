#!/usr/bin/env perl 


# searches for a device with the given name (defaulting to ES-9) and returns it's long "internal name"
# that can be used to pass to jackd's -d (device) param so it can connect to a non-default audio device
# if no match is found, prints nothing

use strict;

my $n;
if ( $1 ) {
    $n = $1;
} else  {
    $n = "ES-9";
}
qx'jackd -X coremidi -d coreaudio -l 2>/dev/null' =~ /name = '\Q$n\E', internal name = '([^']+)'/ and print"$1\n";
