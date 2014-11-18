#!/usr/bin/perl
$input = 'all_satellites.txt';
$dir = '.';
open(FILE, $input) or die "could not open $input, exit.";
while (<FILE>)
{
    chomp;

    # use \ for | otherwise it selects one character
    @fields = split('\|',$_);
    $satellite = $fields[0];
    $satellite =~ s#([a-z|A-Z|\ ]+)([\s*|\-?])(\d{0,2}[A-Z]{0,2}$|[A-Z]?\d{0,2}$)#$1#;


    $number = $fields[0];
    $number =~ s#([a-z|A-Z|\s]+)([\s*|\-?])(\d{0,2}[A-Z]{0,2}$|[A-Z]?\d{0,2}$)#$3#;
    #print "Processing $satellite number $number\n";

    $type = $fields[5];
    next if ! $type =~ /Communications/;
    # read all kml files
    opendir (MYDIR, $dir) or die 'opendir';
    my $body = join ("\n", sort {$a cmp $b}
		grep { ! /^\./ }
                grep { /$satellite\s*$number/i }
		grep { -T "$_" }
		readdir MYDIR
            );
    #print "$body\n";
    my $index=1;
    my @all = split(' ',$body);
    for my $kmlFile (@all) 
    {
       print "$fields[0]\|$index\|$kmlFile\|Ku\n";
       $index++;
    }
    closedir MYDIR;
    
}
close FILE;
