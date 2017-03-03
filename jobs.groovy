mybranch = 'yskrabkou'

job('MNTLAB-' + mybranch + '-main-build-job') 
	{
		logRotator 
		{
        	numToKeep(10)        
        }

		parameters
        {        	 
            activeChoiceParam('BRANCH_NAME')
	        {              
                choiceType('SINGLE_SELECT')

                groovyScript
                {
                    script('return ["yskrabkou", "master"]')
                }
            }
        

         activeChoiceReactiveParam('JOB_SELECT')
          {
         	choiceType('CHECKBOX')

            groovyScript 
            {
            script('return ["MNTLAB-yskrabkou-child1-build-job", "MNTLAB-yskrabkou-child2-build-job", "MNTLAB-yskrabkou-child3-build-job", "MNTLAB-yskrabkou-child4-build-job"]')
            }
    	}

    	 } 

    	 scm
        {
          github('MNT-Lab/mntlab-dsl', '$BRANCH_NAME')
        }

          steps
           {
       			downstreamParameterized 
       			{
               		trigger('$JOB_SELECT') 
               		{
		            	block 
		            	{
                    		buildStepFailure('FAILURE')
                    		failure('FAILURE')
                    		unstable('UNSTABLE')
                        }
               			parameters 
               			{
               			    predefinedProp('BRANCH_NAME', '$BRANCH_NAME')
                        }
                    }
                }
            }
    }


for (i in 1..4) 
{
	job('MNTLAB-' + mybranch + '-child' + i + '-build-job') 
	{
		logRotator 
		{
        	numToKeep(10)        
        }

		parameters
		{
			   	    	//stringParam('BRANCH_NAME1')

			gitParam('BRANCH_NAME')
			  {
               type('BRANCH')  
               defaultValue('origin/yskrabkou')                  
              }
    

   	    	//activeChoiceParam('BRANCH_NAME')
	        //{
              //  description('Allows to choose branch from repository')
               // choiceType('SINGLE_SELECT')

              //  groovyScript
               // {
               //     script('def getTags = ("git ls-remote -t -h https://github.com/MNT-Lab/mntlab-dsl.git").execute();def branchList  = getTags.text.readLines().collect {it.split()[1].replaceAll("refs/heads/", "")}.unique(); branchList  = branchList .reverse(); return branchList;')
              //  }


     // #cp ../MNTLAB-yskrabkou-main-build-job/jobs.groovy jobs.groovy
     //cp ../MNTLAB-yskrabkou-main-build-job/script.sh script.sh	 
         //   }
        }

         scm
        {
           github('MNT-Lab/mntlab-dsl', '${BRANCH_NAME}')
        }

        steps
        {
        	shell('''BRANCH_NAME=${BRANCH_NAME##*/}
        		rm -f *.tar.gz 
        	             	
        	      chmod +x script.sh
        	      ./script.sh > output.txt
        	      cat output.txt
        	      tar czvf $BRANCH_NAME"_dsl_script.tar.gz" jobs.groovy script.sh 	
				'''
          	     )
        }
        
        publishers 
        {
        	archiveArtifacts('output.txt,*.tar.gz')
        }
        
	}
}