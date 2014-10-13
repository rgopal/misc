BEGIN {
# How many lines
    FS="|"
    lines=0;
}
$6 ~ /GEO/ || NR < 2 {
# this code is executed once for each line
# increase the number of files
    #print $4 "|" $5 "|" $6
    # remove parenthetical text from new name field
    first=$1
    if (NR < 2)
      first="Name 1"
    # Inmarsat shows up with weird character
    sub("[\\t| ]+\(.*\)","",first) 
    sub("Wideband Global Satcom", "WGS", first)
    sub("UHF Follow-On", "", first)
    # remove all double quotes
    gsub("[ ]*\"", "", first)
    printf first
    src=1
    # exclude extra fields
    for (i=1; i< 34; i++) 
    { 
       printf "|%s", $i 
       # for first line put source number
       if (i > 27 && NR < 2)
          printf "%s",src++
       # print field number for first line
       if (NR < 2)
          printf " %s",i+1
    }
    # end of line
    printf "\n"
    lines++;
}

END {
# end, now output the total
    print lines " lines read";
}
