package org.example.spring_task.utils;

import java.util.Base64;
import java.util.Base64.Decoder;

public class GitHubApiBuilder {

  public final static String apiLinkMain = "https://api.github.com";
  public final static String apiRepos = "/repos";

  public final static String apiOrgs = "/orgs/";

  public final static String apiForWordSearchInRepos = "https://api.github.com/search/repositories?q=";
  public final static String apiOrgName = "org:";
  public final static String apiSearchInReadme= "in:readme";

//  https://api.github.com/search/repositories?q=org:skibidi-toilets+hello+in:readme"

//
//  public final static String apiCodingAlgo = "base64";
//  public final static Decoder apiDecoder = Base64.getDecoder();
}
