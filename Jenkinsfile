pipeline {
  environment {
     ENV="stg"   //Change the environment accordingly ex: stg for staging and  pr for production
   	 PROJECT = "mobitel_pipeline"
 	 APP_NAME = "sample"      //Change the application name , which will also be the deployment name
     CIR = "${ENV}-docker-reg.mobitel.lk"
     CIR_USER = 'natheeshshaan@gmail.com'
     CIR_PW = 'Qwerty@123'
     KUB_NAMESPACE = "deployments"               //Change the namespace accordingly
     IMAGE_TAG = "natheeshan/${APP_NAME}:${ENV}.${env.BUILD_NUMBER}"
     EXPOSE_PORT="8080"                    //Change the service expose port accordingly
     HARBOUR_SECRET="harbor-stg"              //Change the harbour secret name accordingly
     
    }
    agent none 
    stages {  

             
      stage('Build & test') {
        agent {
              docker {
           		image 'maven:3.9.6-amazoncorretto-21'
           		args '-v /root/.m2:/root/.m2'
                }
        }
        steps {
            sh 'mvn clean install -Dmaven.test.skip=true'
            // Publish JUnit results
        }
      }  

	  stage('Building & Deploy Image') {
      	agent any
		    steps{
              sh '''
              
          		docker login -u ${CIR_USER} -p ${CIR_PW} 
          		mkdir -p dockerImage/
		  		cp Dockerfile dockerImage/
         		cp target/*.jar dockerImage/
		     	docker build --tag=${IMAGE_TAG} dockerImage/.
				docker push ${IMAGE_TAG}
         '''
        	}
     }
      
      stage('Trivy-Scan') {
            agent {
                docker {
                    image 'aquasec/trivy:latest'
                    args '--entrypoint="" -v /var/jenkins_home/trivy-reports:/reports -v trivy-cache:/root/.cache/ '
                }
            }
            steps {
                script {
                    sh "trivy image --no-progress  --timeout 15m -f table  ${IMAGE_TAG}"

                }
            }
           }
      
      stage ('Remove local Image'){
      agent any
           steps {
                sh 'docker image rm ${IMAGE_TAG}'
            }
      
      }
     
        stage('Deploy cluster') {
              agent {
                 docker {
                       //image "${ENV}-docker-reg.mobitel.lk/mobitel_pipeline/cicdtools:1"
                   	   image 'inovadockerimages/cicdtools:latest' 
                         args '-v /root/.cert:/root/.cert'   
                        }
                    }
             steps {
               
               sh '''
               
               mkdir -p /root/.kube/
               cp /var/jenkins_home/config /root/.kube/
               '''
               script {
               def isDeployed = sh(returnStatus: true, script: 'kubectl -n ${KUB_NAMESPACE} set image deployment/${APP_NAME}  ${APP_NAME}=${IMAGE_TAG}  --record ')
                if (isDeployed != 0) {
                        sh '''
                        kubectl -n ${KUB_NAMESPACE} create deployment ${APP_NAME}  --image=${IMAGE_TAG} 
               			kubectl -n ${KUB_NAMESPACE} expose deployment ${APP_NAME}  --name=${APP_NAME} --port=${EXPOSE_PORT}
                        
                        ## Replace the harbour image policy secret name
			   			kubectl -n ${KUB_NAMESPACE} patch deployment ${APP_NAME} --patch \'{"spec": {"template": {"spec": {"imagePullSecrets": [{"name": "'"${HARBOUR_SECRET}"'" }]}}}}\'
                        
                        ## Set resource limits
            			kubectl -n ${KUB_NAMESPACE} patch deployment ${APP_NAME} --type=\'json\' -p=\'[{"op": "add","path": "/spec/template/spec/containers/0/resources","value": {"limits": {"memory": "512Mi"}}}]\'
						
						## Replace the deployment name
					    ##kubectl -n ${KUB_NAMESPACE} patch deployment $deploy -p \'{"spec":{"template":{"spec":{"containers":[{"name": "'"${APP_NAME}"'","imagePullPolicy":"IfNotPresent"}]}}}}\'
                        
                        '''
                    }
               }              
            }
          }
      
     stage('GIT URL & Committer Email') {
          agent any  
          steps {
              script {
                     checkout scm
                
                  // Retrieve the GIT URL from the checked-out repository
                GIT_URL = sh(script: 'git config --get remote.${APP_NAME}.url', returnStdout: true).trim()
                    echo "GIT URL: ${env.GIT_URL}"
                
                  // Extract the committer's email from the latest commit
                  env.COMMITTER_EMAIL = sh(
                      script: "git log -1 --pretty=format:'%ce'",
                      returnStdout: true
                  ).trim()
                  echo "Committer Email: ${env.COMMITTER_EMAIL}"
              }
          }
      }
      
      
         stage('Set Deployment Description Annotation') {
            agent {
                 docker {
                       image 'inovadockerimages/cicdtools:latest' 
                         args '-v /root/.cert:/root/.cert'   
                        }
                    }
            steps {
                  
                  //set kube config token
                 sh '''
                 mkdir -p /root/.kube/
                 cp /root/.cert/${ENV}/config /root/.kube/
                 '''
              
                script {
                    // Bash script to set the description if it doesn't exist
                    sh '''
                    #!/bin/bash
                 
                    echo "GIT URL: ${GIT_URL} "
                    echo "JENKINS URL: ${BUILD_URL} "
                    DESCRIPTION="Jenkins URL: ${BUILD_URL}  GIT URL: ${GIT_URL}"

                    # Check if the deployment has the field.cattle.io/description annotation
                    CURRENT_DESCRIPTION=\$(kubectl -n ${KUB_NAMESPACE} get deployment ${APP_NAME} -o jsonpath='{.metadata.annotations.field\\.cattle\\.io/description}')

                    if [ -z "\$CURRENT_DESCRIPTION" ]; then
                      echo "No field.cattle.io/description found. Setting the description."
                      # Add the annotation to the deployment
                      kubectl -n ${KUB_NAMESPACE} annotate deployment ${APP_NAME} field.cattle.io/description="\${DESCRIPTION}" --overwrite
                    else
                      echo "field.cattle.io/description already exists: \$CURRENT_DESCRIPTION"
                    fi
                    '''
                }
            }
        }     
      
      
      
      
      
        }
            post {
            success {
              mail to: 'natheeshans.ou@mobitel.lk',
                         subject: "${env.JOB_NAME} - Build  ${env.BUILD_NUMBER} - Success!",
                       body: """${env.JOB_NAME} - Build  ${env.BUILD_NUMBER} - Success:
                             Check console output at ${env.BUILD_URL} to view the results."""
                    }
            failure {
                   mail to: "natheeshans.ou@mobitel.lk",
                   cc: 'natheeshans.ou@mobitel.lk',
                       subject: "${env.JOB_NAME} - Build  ${env.BUILD_NUMBER} - Failed!",
                        body: """${env.JOB_NAME} - Build  ${env.BUILD_NUMBER} - Failed:
                             Check console output at ${env.BUILD_URL} to view the results."""
                  }
             }
            
}
