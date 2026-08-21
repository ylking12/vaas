import { test } from 'node:test'
import assert from 'node:assert/strict'
import { createLatestOnlyExecutor } from './latestOnly.js'

function deferred() {
  let resolve
  const promise = new Promise(r => { resolve = r })
  return { promise, resolve }
}

test('only applies the latest async result when older timeline requests finish later', async () => {
  const runLatest = createLatestOnlyExecutor()
  const first = deferred()
  const second = deferred()
  const applied = []

  const firstRun = runLatest(() => first.promise, value => applied.push(value))
  const secondRun = runLatest(() => second.promise, value => applied.push(value))

  second.resolve('latest')
  assert.equal((await secondRun).applied, true)
  assert.deepEqual(applied, ['latest'])

  first.resolve('stale')
  assert.equal((await firstRun).applied, false)
  assert.deepEqual(applied, ['latest'])
})
