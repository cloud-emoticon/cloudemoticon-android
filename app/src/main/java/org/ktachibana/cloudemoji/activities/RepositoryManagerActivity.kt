package org.ktachibana.cloudemoji.activities


import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView

import com.afollestad.materialdialogs.MaterialDialog
import com.melnykov.fab.FloatingActionButton

import org.apache.commons.io.FilenameUtils
import org.greenrobot.eventbus.Subscribe
import org.ktachibana.cloudemoji.BaseActivity
import org.ktachibana.cloudemoji.BaseApplication
import org.ktachibana.cloudemoji.R
import org.ktachibana.cloudemoji.adapters.RepositoryListViewAdapter
import org.ktachibana.cloudemoji.database.Repository
import org.ktachibana.cloudemoji.database.RepositoryDao
import org.ktachibana.cloudemoji.database.RepositoryFactory
import org.ktachibana.cloudemoji.events.NetworkUnavailableEvent
import org.ktachibana.cloudemoji.events.RepositoryBeginEditingEvent
import org.ktachibana.cloudemoji.events.RepositoryDownloadFailedEvent
import org.ktachibana.cloudemoji.events.RepositoryDownloadedEvent
import org.ktachibana.cloudemoji.events.RepositoryExportedEvent
import org.ktachibana.cloudemoji.events.RepositoryInvalidFormatEvent
import org.ktachibana.cloudemoji.ui.MultiInputMaterialDialogBuilder

import butterknife.BindView
import butterknife.ButterKnife

class RepositoryManagerActivity : BaseActivity() {
    @BindView(R.id.repositoryListView)
    lateinit var mRepositoryListView: ListView

    @BindView(R.id.emptyView)
    lateinit var mRepositoryEmptyView: RelativeLayout

    @BindView(R.id.emptyViewTextView)
    lateinit var mEmptyViewTextView: TextView

    @BindView(R.id.fab)
    lateinit var mFab: FloatingActionButton

    private var mAdapter: RepositoryListViewAdapter? = null

    private var mRepositoryDao: RepositoryDao? = null

    public override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        setContentView(R.layout.activity_repository_manager)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        ButterKnife.bind(this)

        // Setup contents
        mRepositoryListView!!.emptyView = mRepositoryEmptyView
        mEmptyViewTextView!!.text = getString(R.string.no_repo_prompt)

        this.mAdapter = RepositoryListViewAdapter(this@RepositoryManagerActivity)
        this.mRepositoryDao = BaseApplication.database()!!.repositoryDao()

        mRepositoryListView!!.adapter = mAdapter
        mFab!!.setImageDrawable(this.resources.getDrawable(R.drawable.ic_fab_create))
        mFab!!.attachToListView(mRepositoryListView!!)
        mFab!!.setOnClickListener { popupAddRepositoryDialog("") }

    }

    fun popupAddRepositoryDialog(passedInUrl: String) {
        /**
         * AddRepositoryDialogFragment fragment = AddRepositoryDialogFragment.newInstance(passedInUrl);
         * fragment.show(getFragmentManager(), "add_repository");
         */
        MultiInputMaterialDialogBuilder(this@RepositoryManagerActivity)
                .addInput(passedInUrl, getString(R.string.repo_url), MultiInputMaterialDialogBuilder.InputValidator { input ->
                    // Get URL and extension
                    val url = input.toString()
                    val extension = FilenameUtils.getExtension(url)

                    // Detect duplicate URL
                    if (mRepositoryDao!!.exists(url)) {
                        return@InputValidator getString(R.string.duplicate_url)
                    }

                    // Detect incorrect file format
                    if (!(extension == "xml" || extension == "json")) {
                        getString(R.string.invalid_repo_format)
                    } else {
                        null
                    }
                })
                .addInput(null, getString(R.string.alias))
                .inputs { dialog, inputs, allInputsValidated ->
                    if (allInputsValidated) {
                        val url = inputs[0].toString()
                        val alias = inputs[1].toString()

                        // Create and save repository to database
                        val repository = RepositoryFactory.newRepository(url, alias)
                        mRepositoryDao!!.add(repository)

                        mAdapter!!.updateRepositories()
                    }
                }
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .title(R.string.add_repo)
                .show()
    }

    @Subscribe
    fun handle(event: RepositoryDownloadedEvent) {
        showSnackBar(event.repository.alias + " " + getString(R.string.downloaded))
        mAdapter!!.updateRepositories()
    }

    @Subscribe
    fun handle(event: RepositoryDownloadFailedEvent) {
        showSnackBar(event.throwable.localizedMessage)
    }

    @Subscribe
    fun handle(event: RepositoryBeginEditingEvent) {
        val repository = event.repository
        MultiInputMaterialDialogBuilder(this@RepositoryManagerActivity)
                .addInput(repository.alias, getString(R.string.alias))
                .inputs { dialog, inputs, allInputsValidated ->
                    val alias = inputs[0].toString()

                    // Get the new alias and SAVE
                    mRepositoryDao!!.update(
                            repository.copy(alias = alias)
                    )

                    mAdapter!!.updateRepositories()
                }
                .title(R.string.edit_repository)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .show()
    }

    @Subscribe
    fun handle(event: NetworkUnavailableEvent) {
        showSnackBar(R.string.bad_conn)
    }

    @Subscribe
    fun handle(exportedEvent: RepositoryExportedEvent) {
        showSnackBar(exportedEvent.path)
    }

    @Subscribe
    fun handle(event: RepositoryInvalidFormatEvent) {
        showSnackBar(getString(R.string.invalid_repo_format) + event.type)
    }
}