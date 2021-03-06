import com.oumuo.lookup.InitWebSite

import com.oumuo.central.InitPerson
import com.oumuo.lookup.InitCountryStateCity
import com.oumuo.lookup.InitCountryLookup
import com.oumuo.central.InitSpringSecurity
import com.oumuo.central.InitOrganization
import com.oumuo.central.InitProgram
import com.oumuo.central.InitCourse
import com.oumuo.central.InitCatalog
import com.oumuo.central.InitRankingItem
import com.oumuo.central.InitCourseRelation
import com.oumuo.central.InitLearning
import com.oumuo.central.InitSyllabus
import com.oumuo.central.InitAssessment
import com.oumuo.central.InitLearningAssessment
import com.oumuo.central.InitContent
import com.oumuo.central.InitAssessmentItem
import com.oumuo.central.InitAuthorship
import com.oumuo.central.InitTerm
import com.oumuo.central.InitStudentProgram
import com.oumuo.central.InitEvent
import com.oumuo.central.InitClazs
import com.oumuo.central.InitClassSession       // includes Instruction
import com.oumuo.central.InitEnrollment
import com.oumuo.central.InitLearningRelation
import com.oumuo.central.InitLocation
import com.oumuo.central.InitLocationUse
import com.oumuo.central.InitPersonInstruction
import com.oumuo.central.InitPersonContent
import com.oumuo.central.InitPersonAssessment
import com.oumuo.central.InitPersonContentAssessment
import com.oumuo.central.InitPersonAssessmentItem
import com.oumuo.central.InitPersonContentAssessmentItem
import com.oumuo.central.InitJob
import com.oumuo.central.InitPersonJob
import com.oumuo.central.InitJobAvailability
import com.oumuo.central.InitPosition
import com.oumuo.central.InitPositionProgram
import com.oumuo.central.InitPersonResource

class BootStrap {
    def aclService
    def aclUtilService
    def objectIdentityRetrievalStrategy
    
    def init = { servletContext ->
        println "aclService - ${aclService}"
        // keep all database table load code in src/groovy
           
        // need to pass these to plain groovy classes which don't get auto service inject
        // they get stored as static so that other Inits can use them
       
        new InitSpringSecurity()
        .load(aclUtilService, aclService, objectIdentityRetrievalStrategy)
        new InitPerson().load()
        InitCountryLookup.load()

        InitCountryStateCity.load()
       
        InitWebSite.load()

        new InitOrganization().load()
        // Acl services are reused from InitSpringSecurity (static fields)
          
        new InitProgram().load()
        new InitCourse().load()
        new InitCatalog().load()
        new InitRankingItem().load()
        new InitCourseRelation().load()
        new InitLearning().load()
        new InitSyllabus().load()
        new InitAssessment().load()
        new InitLearningAssessment().load()
        new InitContent().load()
        new InitAssessmentItem().load()
        new InitAuthorship().load()
        new InitTerm().load()
        new InitStudentProgram().load()
        new InitEvent().load()
        new InitClazs().load()
        new InitClassSession().load()
        new InitEnrollment().load()
        new InitLearningRelation().load()
        new InitLocation().load()
        new InitLocationUse().load()
        new InitPersonInstruction().load()
        new InitPersonContent().load()
        new InitPersonAssessment().load()
        new InitPersonContentAssessment().load()
        new InitPersonAssessmentItem().load()
        new InitJob().load()
        new InitPersonJob().load()
        new InitJobAvailability().load()
        new InitPosition().load()
        new InitPositionProgram().load()
        new InitPersonResource().load()
    }
   
    def destory = {
        
    }
    
    
}