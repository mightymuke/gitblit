package com.gitblit.wicket.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.gitblit.GitBlit;
import com.gitblit.utils.JGitUtils;
import com.gitblit.utils.StringUtils;
import com.gitblit.wicket.GitBlitWebSession;
import com.gitblit.wicket.WicketUtils;

public class PatchPage extends WebPage {

	public PatchPage(PageParameters params) {
		super(params);

		if (!params.containsKey("r")) {
			GitBlitWebSession.get().cacheErrorMessage("Repository not specified!");
			redirectToInterceptPage(new RepositoriesPage());
			return;
		}
		
		final String repositoryName = WicketUtils.getRepositoryName(params);
		final String baseObjectId = WicketUtils.getBaseObjectId(params);
		final String objectId = WicketUtils.getObject(params);
		final String blobPath = WicketUtils.getPath(params);

		Repository r = GitBlit.self().getRepository(repositoryName);
		if (r == null) {
			GitBlitWebSession.get().cacheErrorMessage("Can not load repository " + repositoryName);
			redirectToInterceptPage(new RepositoriesPage());
			return;
		}

		RevCommit commit = JGitUtils.getCommit(r, objectId);
		if (commit == null) {
			GitBlitWebSession.get().cacheErrorMessage("Commit is null");
			redirectToInterceptPage(new RepositoriesPage());
			return;
		}
		
		String patch;
		if (StringUtils.isEmpty(baseObjectId)) {
			patch = JGitUtils.getCommitPatch(r, commit, blobPath);
		} else {
			RevCommit baseCommit = JGitUtils.getCommit(r, baseObjectId);
			patch = JGitUtils.getCommitPatch(r, baseCommit, commit, blobPath);			
		}
		add(new Label("patchText", patch));
		r.close();
	}
}