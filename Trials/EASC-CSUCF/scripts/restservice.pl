#!/usr/bin/perl

use HTTP::Daemon;
use threads;
use POSIX qw(strftime);

my $webServer;

my $d = HTTP::Daemon->new(LocalAddr => $ARGV[0],
                          LocalPort => 8500) || die;

#print "Web Server started!\n";
#print "Server Address: ", $d->sockhost(), "\n";
#print "Server Port: ", $d->sockport(), "\n";


while (my $c = $d->accept) {
    threads->create(\&process_one_req, $c)->detach();
}

sub process_one_req {
    my $c = shift;
    my $r = $c->get_request;
    if ($r) {
        if ($r->method eq "GET") {
            my $path = $r->url->path();
		my $response = HTTP::Response->new(200);
		$response->header("Content-Type" => "text/html");
		if ($path eq "/changeWM"){
			my %queryforms = $r->url->query_form();
			my $activity = $queryforms{'activity'};
			my $wm = $queryforms{'wm'};
			my $datacenter = $queryforms{'datacenter'};
			if ($activity eq "VideoTranscoding"){
				system("scripts/WMSwitchvt.sh $wm $datacenter &");
			}elsif ($activity eq "WebCrawling"){
				system("scripts/WMSwitchwc.sh $wm $datacenter &");
			}
			$response->content("Switching to: $wm");
		}elsif ($path eq "/getWMStatus"){
			my %queryforms = $r->url->query_form();
			my $activity = $queryforms{'activity'};
			my $wm = $queryforms{'wm'};
			my $date = strftime "%Y-%m-%d", localtime;
			my $result = `sqlite3 -init resource/init.sql  resource/dc4cities.sqlt \"select status from working_modes where activity='$activity' and date='$date' and wm='$wm';\" 2>/dev/null`;
			if ($result !~ /canvia/){
				$result="canviant";
			}
			$response->content($result);
		}elsif ($path eq "/getWMStatistics"){
			my %queryforms = $r->url->query_form();
			my $activity = $queryforms{'activity'};
			my $type = $queryforms{'type'};
			my $result = `scripts/getWMStatistics.sh $activity $type`;
			$response->content($result);
		}
            	$c->send_response($response);
        }
    }
    $c->close;
    undef($c);
}
