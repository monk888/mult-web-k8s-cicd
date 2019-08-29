def registry = "192.168.7.140"


String0 = "$params.mjob"
def project = "sx"

def git_address = "http://192.168.7.94/michong/multi-web.git"

def secret_name = "registry-pull-secret"
def docker_registry_auth = "ca1d3f4a-01e7-405d-9188-b612f897a773" 
def git_auth = "2cb6a844-fe40-4cbf-a342-054611510828"
def k8s_auth = "cc8ec473-66cd-4494-b696-27c89cadb786" 
podTemplate(label: 'jenkins-slave', cloud: 'kubernetes', containers: [
containerTemplate(
name: 'jnlp', 
image: "${registry}/sx/jenkins-slave-jdk:1.8"
),
],
volumes: [ 
hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
hostPathVolume(mountPath: '/usr/bin/docker', hostPath: '/usr/bin/docker')
],
) 

{
node("jenkins-slave"){
    // 第一步
   stage('拉取代码'){
   checkout([$class: 'GitSCM', branches: [[name: '${Branch}']], userRemoteConfigs: [[credentialsId: "${git_auth}", url: "${git_address}"]]])
    }
   stage('查看变量') {

     echo "mjob----------->${mjob}"  
     script {
      def apps = "${mjob}".split(",")
    //   def apps = ['China','US','HongKong']
       for (int i = 0; i < apps.size(); ++i) {
          echo "${apps[i]}"
         }
      } 
    }
    
   // 第二步
   stage('代码编译'){
      sh  """
      sed  -i 's#senxiang_content#${content}#g' web1/src/main/java/com/example/demo/HomeController.java
      sed  -i 's#senxiang_content#${content}#g' web2/src/main/java/com/example/demo/HomeController.java
      sed  -i 's#senxiang_content#${content}#g' web3/src/main/java/com/example/demo/HomeController.java
      mvn clean package -Dmaven.test.skip=true
      """
     }
   
   // 第三步
   stage ('代码扫描') {
    script {
      def proj = "${mjob}".split(",")
      for (int i = 0; i < proj.size(); ++i) {
      sh """
        /usr/local/sonar-scanner-4.0.0.1744-linux/bin/sonar-scanner \
                      -Dsonar.projectKey=${ProjectName}-"${proj[i]}" \
                      -Dsonar.projectName=${ProjectName}-"${proj[i]}" \
                      -Dsonar.login=admin \
                      -Dsonar.password=admin \
                      -Dsonar.sources="${proj[i]}"/src \
                      -Dsonar.host.url=${SonarServer} \
                      -Dsonar.java.binaries="${proj[i]}"/target/classes \
        """
        }
      }
    }

   
   // 第四步
   stage('构建镜像'){
    script {
      def app_name = "${mjob}".split(",")
      for (int i = 0; i < app_name.size(); ++i) {
        echo "${app_name[i]}"
        def image_name = "${registry}/${project}/${app_name[i]}:${version}" 
        withCredentials([usernamePassword(credentialsId: "${docker_registry_auth}", passwordVariable: 'password', usernameVariable: 'username')]) {
          sh """
            echo '
              FROM 192.168.7.140/sx/openjdk:8-jdk-alpine
              MAINTAINER michong
              COPY "${app_name[i]}"/target/"${app_name[i]}".jar /"${app_name[i]}".jar
              ENTRYPOINT  ["java","-jar","${app_name[i]}.jar"] 
              ' > Dockerfile
            ls 
            ls "${app_name[i]}"/target
            docker build -t ${image_name} .
            docker login -u ${username} -p '${password}' ${registry}
            docker push ${image_name}
           """
          }
        } 
      }
    }

   // 第五步
   stage('部署到K8S平台'){
     script {
       def app_n = "${mjob}".split(",")
       for (int i = 0; i < app_n.size(); ++i) {
         def image_name = "${registry}/${project}/${app_n[i]}:${version}" 
         sh """
           sed  's#\$IMAGE_NAME#${image_name}#' deploy.yaml > "deploy-${app_n[i]}.yaml"
           sed -i 's#\$SECRET_NAME#${secret_name}#' "deploy-${app_n[i]}.yaml"
           sed -i 's#\$SERVICE#${app_n[i]}#g' "deploy-${app_n[i]}.yaml"
           sed -i 's#\$REPLICAS#${replicas}#' "deploy-${app_n[i]}.yaml"
         """
         kubernetesDeploy configs: "deploy-${app_n[i]}.yaml", kubeconfigId: "${k8s_auth}"
        }
      }
    }

  }
}