
# return number of transponders and specific band in str
# separated by |
function getBand(band,str)
{
    reg = "[0-9]+ " band "[\- ][Bb]and"
    string = "||"
    if (match(str,reg) != 0)
    {
       found=substr(str,RSTART,RLENGTH)
       number = found 
       sub("[ ]*[a-z|A-Z|\- ]+[ ]*","",number)
       band = found 
       sub("[ ]*[0-9]+[ ]*","",band)
       sub("-[Bb]and","",band)
      
       string = "|" band "|" number
    }
        return string
}

BEGIN {
# How many lines
    FS="|"
    lines=0
    FIELDS = 33
}
$6 ~ /GEO/ || NR < 2 {
# this code is executed once for each line
# increase the number of files

    # remove parenthetical text from new name field
    first=$1
    if (NR < 2)
      first="Name 1"
    # Inmarsat shows up with weird character

    sub("[\\t| ]+\(.*\)","",first) 
    sub("Wideband Global Satcom", "WGS", first)
    sub("UHF Follow-On", "", first)
    # remove spaces before and after | in first field (Amazonas 1 had problem)
    gsub("[ ]*|[ ]*","|",first)
    # remove all double quotes
    gsub("[ ]*\"", "", first)

    printf first
    src=1
    # exclude extra fields
    for (i=1; i<= FIELDS ; i++) 
    { 
       # remove | from GSAT and INSAT records (India)
       sub("|","",$i)
       printf "|%s", $i 


       # for first line put source number
       if (i > 27 && NR < 2)
          printf "%s",src++

       # print field number for first line
       if (NR < 2)
       {
          printf " %s",i+1
       }
    }
    # end of line
    if (NR < 2)
    {
       printf "|EIRP %s", FIELDS+2
       printf "|Gain %s", FIELDS+3
       printf "|BAND1 %s|Transponders1 %s", FIELDS+4, FIELDS+5
       printf "|BAND2 %s|Transponders2 %s", FIELDS+6, FIELDS+7
       printf "|BAND3 %s|Transponders3 %s", FIELDS+8, FIELDS+9
    } else
    {
       printf "|40|25"

        if (match($25,"Hybrid Ku-Ka") != 0)
            printf "|||Ku||Ka||"
        else
        {

       printf getBand("C",$25)
       printf getBand("Ku",$25)
       printf getBand("Ka",$25)
        }
    }
    

    printf "\n"
}

END {
# end, now output the total
}
