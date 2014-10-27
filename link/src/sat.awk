
# return number of transponders and specific band in str
# separated by |
function getBand(band,str,eirp,gt)
{
    reg = "[0-9]+ " band "[\- ][Bb]and"
    string = "||||"
    if (match(str,reg) != 0)
    {
       found=substr(str,RSTART,RLENGTH)
       number = found 
       sub("[ ]*[a-z|A-Z|\- ]+[ ]*","",number)
       band = found 
       sub("[ ]*[0-9]+[ ]*","",band)
       sub("-[Bb]and","",band)
      
       string = "|" band "|" number "|" eirp "|" gt
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
    gsub("^[ ]","",first)
    gsub("[ ]*$","",first)
    # remove all double quotes
    gsub("[ ]*\"", "", first)

    printf first
    src=1
    # exclude extra fields
    for (i=1; i<= FIELDS ; i++) 
    { 
       # remove | from GSAT and INSAT records (India)
       sub("\|","",$i)
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
       printf "|BAND C %s|Transponders C %s|EIRP C %s|GT C %s", FIELDS+2, FIELDS+3, FIELDS+4,FIELDS+5
       printf "|BAND X %s|Transponders X %s|EIRP X %s|GT X %s", FIELDS+6, FIELDS+7, FIELDS+8, FIELDS+9
       printf "|BAND Ku %s|Transponders Ku %s|EIRP Ku %s|GT Ku %s", FIELDS+10, FIELDS+11, FIELDS+12, FIELDS+13
       printf "|BAND Ka %s|Transponders Ka %s|EIRP Ka %s|GT Ka %s", FIELDS+14, FIELDS+15, FIELDS+16, FIELDS+17
    } else
    {

        if (match($25,"Hybrid Ku-Ka") != 0)
            printf "|||||Ku||Ka||"
        else
        {

       printf getBand("C",$25,40,25)
       printf getBand("X",$25,45,30)
       printf getBand("Ku",$25,50,35)
       printf getBand("Ka",$25,55,40)
        }
    }
    

    printf "\n"
}

END {
# end, now output the total
}
