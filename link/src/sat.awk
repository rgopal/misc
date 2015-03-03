
# return number of transponders and specific band in str
# separated by |
function getBand(band,str,txAnt, txPower, rxAnt, rxNF)
{
    reg = "[0-9]+ " band "[\- ][Bb]and"
    string = "||||||"
    if (match(str,reg) != 0)
    {
       found=substr(str,RSTART,RLENGTH)
       number = found 
       sub("[ ]*[a-z|A-Z|\- ]+[ ]*","",number)
       band = found 
       sub("[ ]*[0-9]+[ ]*","",band)
       sub("-[Bb]and","",band)
      
       string = "|" band "|" number "|" txAnt "|" txPower "|" rxAnt "|" rxNF
    } else {
    # just look for band (no transponders)
      reg =  band "[\- ][Bb]and"
      if (match(str,reg) != 0)
      {
         found=substr(str,RSTART,RLENGTH)
         band = found 
         sub("[\- ][Bb]and","",band)
      
         string = "|" band "|" number "|" txAnt "|" txPower "|" rxAnt "|" rxNF
      }
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
       printf "|BAND C %s|Transponders C %s|TxAnt C %s|TxPower C %s|RxAnt C %s|RxNF C %s", FIELDS+2, FIELDS+3, FIELDS+4,FIELDS+5,FIELDS+6,FIELDS+7
       printf "|BAND X %s|Transponders X %s|TxAnt X %s|TxPower X %s|RxAnt X %s|RxNF X %s", FIELDS+8, FIELDS+9, FIELDS+10, FIELDS+11, FIELDS+12, FIELDS+13
       printf "|BAND Ku %s|Transponders Ku %s|TxAnt Ku %s|TxPower Ku %s|RxAnt Ku %s|RxNF Ku %s", FIELDS+14, FIELDS+15, FIELDS+16, FIELDS+17, FIELDS+18, FIELDS+19
       printf "|BAND Ka %s|Transponders Ka %s|TxAnt Ka %s|TxPower Ka %s|RxAnt Ka %s|RxNF Ka %s", FIELDS+20, FIELDS+21, FIELDS+22, FIELDS+23, FIELDS+24, FIELDS+25
    } else
    {

        # for AMC-15 on line 25 and Amos 3
        if (match($25,"Hybrid Ku-Ka") != 0 ||
             match($25,"Ku and Ka band") != 0 )
            printf "|||||||||||||Ku|0|3.8|50|3|2.2|Ka|0|3.7|44|2.5|2.3"
        else if (match($1,"Spaceway") != 0) 
        { 
            printf "|||||||||||||||||||Ka|100|3.7|44|2.5|2.3"
        } else
        {

          printf getBand("C",$25,4,40,4,2)
          printf getBand("X",$25,3.9,45,3.5,2.1)
          printf getBand("Ku",$25,3.8,50,3,2.2)
          printf getBand("Ka",$25,3.7,55,2.5,2.3)
        }
    }
   
    printf "\n"
}

END {
# end, now output the total
}
